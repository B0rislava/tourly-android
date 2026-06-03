package com.tourly.app.chat.data.repository

import com.tourly.app.BuildConfig
import com.tourly.app.chat.data.dto.ChatMessageDto
import com.tourly.app.chat.domain.exception.ChatException
import com.tourly.app.chat.domain.model.Message
import com.tourly.app.chat.domain.repository.ChatRepository
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.core.util.DateUtils
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.util.UUID.randomUUID
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val stompClient: StompClient,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase
) : ChatRepository {

    private var stompSession: StompSessionWithKxSerialization? = null
    
    private val _messages = MutableStateFlow<Map<Long, List<ChatMessageDto>>>(emptyMap())
    private val _currentUserId = MutableStateFlow<Long>(-1L)

    init {
        // Refresh user profile on repository creation
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            refreshUserProfile()
        }
    }

    private suspend fun refreshUserProfile() {
        val result = getUserProfileUseCase()
        if (result is Result.Success) {
            Timber.d("ChatRepository: User profile refreshed. Current ID: ${result.data.id}")
            _currentUserId.value = result.data.id
        } else {
            Timber.e("ChatRepository: Failed to refresh user profile")
        }
    }

    private suspend fun ensureValidUserId(): Long {
        if (_currentUserId.value != -1L) {
            return _currentUserId.value
        }
        Timber.d("ChatRepository: Current user ID is invalid (-1). Refreshing...")
        refreshUserProfile()
        return try {
            withTimeout(3000L) {
                val id = _currentUserId.filter { it != -1L }.first()
                Timber.d("ChatRepository: Resolved valid user ID: $id")
                id
            }
        } catch (e: TimeoutCancellationException) {
            val ex = ChatException.Timeout("Timeout waiting for user profile")
            Timber.e(ex, "ChatRepository: ${ex.message}")
            -1L
        }
    }

    private suspend fun getSession(): StompSessionWithKxSerialization {
        stompSession?.let { return it }

        val baseUrl = BuildConfig.BASE_URL.removeSuffix("/api/")
        val wsUrl = (if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .replace("http", "ws") + "ws/websocket"
        
        Timber.d("Connecting to WebSocket: $wsUrl")
        val session = stompClient.connect(wsUrl)
            .withJsonConversions(Json { ignoreUnknownKeys = true })
        
        stompSession = session
        return session
    }

    override fun getMessages(tourId: Long): Flow<List<Message>> = callbackFlow {
        val userId = ensureValidUserId()
        Timber.d("ChatRepository: Starting message flow with userId=$userId for tour=$tourId")

        // Fetch tour details to get guide ID
        var tourGuideId = -1L
        val tourResult = getTourDetailsUseCase(tourId)
        if (tourResult is Result.Success) {
            tourGuideId = tourResult.data.tourGuideId
            Timber.d("ChatRepository: Found tour guide ID: $tourGuideId")
        }

        // 1. Fetch History Always (Refresh)
        launch {
            Timber.d("ChatRepository: Fetching history for tour $tourId...")
            val historyUrl = "${BuildConfig.BASE_URL}chat/$tourId/messages"
            
            when (val result = NetworkResponseMapper.map<List<ChatMessageDto>> { httpClient.get(historyUrl) }) {
                is Result.Success -> {
                    val history = result.data
                    Timber.d("ChatRepository: Fetched ${history.size} messages from history for tour $tourId")
                    
                    // MERGE with existing optimistic messages checking for duplicates
                    _messages.update { current ->
                        val existing = current[tourId] ?: emptyList()
                        val optimistic = existing.filter { (it.id ?: 0) < 0 }
                        
                        // Keep optimistic messages only if they are NOT in history yet
                        val pendingOptimistic = optimistic.filter { opt ->
                            history.none { real -> 
                                real.content == opt.content && real.senderId == opt.senderId 
                            }
                        }
                        
                        val merged = (history + pendingOptimistic)
                            .distinctBy { it.id }
                            .sortedBy { it.timestamp }
                            
                        Timber.d("ChatRepository: Merged history. Total: ${merged.size} (Pending optimistic: ${pendingOptimistic.size})")
                        current + (tourId to merged)
                    }
                }
                is Result.Error -> {
                    Timber.e("ChatRepository: Failed to fetch history: ${result.message}")
                }
            }
        }

        // 2. Subscribe to real-time updates
        launch {
            try {
                val session = getSession()
                session.subscribe<ChatMessageDto>("/topic/messages/$tourId").collect { dto ->
                    Timber.d("Received STOMP message: ${dto.content}")
                    _messages.update { current ->
                        val tourMsgs = current[tourId] ?: emptyList()
                        
                        // Check if we have an optimistic version (negative ID)
                        val optimisticIndex = tourMsgs.indexOfFirst { 
                             (it.id ?: 0) < 0 && it.content == dto.content && it.senderId == dto.senderId 
                        }

                        if (optimisticIndex != -1) {
                            Timber.d("ChatRepository: Replacing optimistic msg ${tourMsgs[optimisticIndex].id} with real msg ${dto.id}")
                            val mutable = tourMsgs.toMutableList()
                            mutable[optimisticIndex] = dto
                            current + (tourId to mutable)
                        } else {
                            if (tourMsgs.none { it.id == dto.id }) {
                                current + (tourId to (tourMsgs + dto))
                            } else {
                                current
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val ex = ChatException.ConnectionError(e.message ?: "WebSocket subscription error")
                Timber.e(ex, "WebSocket subscription error")
                stompSession = null
            }
        }

        // 3. Reactive mapping
        val job = _currentUserId
            .filter { it != -1L }
            .combine(_messages) { uid, messages ->
                val dtos = messages[tourId] ?: emptyList()
                dtos.map { mapDtoToDomain(it, uid, tourGuideId) }
            }
            .onEach { messages ->
                // Timber.d("ChatRepository: Emitting ${messages.size} messages")
                trySend(messages)
            }
            .launchIn(this)

        awaitClose { job.cancel() }
    }

    override suspend fun sendMessage(tourId: Long, content: String): Result<Unit> {
        return try {
            val userId = ensureValidUserId()
            if (userId == -1L) {
                val ex = ChatException.UserNotFound()
                return Result.Error(code = ex.code, message = ex.message!!)
            }
            
            val user = (getUserProfileUseCase() as? Result.Success)?.data 
                ?: run {
                    val ex = ChatException.UserNotFound()
                    return Result.Error(code = ex.code, message = ex.message!!)
                }

            val session = getSession()
            val dto = ChatMessageDto(
                tourId = tourId,
                senderId = user.id,
                senderName = user.fullName,
                content = content,
                timestamp = LocalDateTime.now().toString()
            )
            
            Timber.d("Sending STOMP message: ${dto.content}")
            session.convertAndSend("/app/chat/$tourId", dto)
            
            // Optimistically update UI immediately
             _messages.update { current ->
                val tourMsgs = current[tourId] ?: emptyList()
                // Use a temporary ID (negative timestamp) to avoid collision with real DB IDs
                val tempId = -(System.currentTimeMillis())
                val tempMsg = dto.copy(id = tempId)
                Timber.d("ChatRepository: Optimistic update for msg: ${dto.content} (tempId=$tempId)")
                current + (tourId to (tourMsgs + tempMsg))
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            val ex = ChatException.SendFailed(e.message ?: "Failed to send message")
            Timber.e(ex, "Send message error")
            stompSession = null
            Result.Error(code = ex.code, message = ex.message ?: "Failed to send message")
        }
    }

    override fun clear() {
        Timber.d("ChatRepository: Clearing session state for logout")
        stompSession = null
        _currentUserId.value = -1L
        _messages.value = emptyMap() // Clear cache to prevent stale messages across users
    }

    private fun mapDtoToDomain(dto: ChatMessageDto, currentUserId: Long, tourGuideId: Long): Message {
        val isFromMe = dto.senderId == currentUserId
        val isGuide = (dto.senderId == tourGuideId) || dto.senderRole.equals("GUIDE", ignoreCase = true)

        return Message(
            id = dto.id?.toString() ?: randomUUID().toString(),
            senderId = dto.senderId,
            senderName = dto.senderName,
            content = dto.content,
            timestamp = DateUtils.parseUtcToLocal(dto.timestamp),
            isFromMe = isFromMe,
            isGuide = isGuide
        )
    }
}
