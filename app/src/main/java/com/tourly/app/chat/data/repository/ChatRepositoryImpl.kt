package com.tourly.app.chat.data.repository

import com.tourly.app.chat.domain.model.Message
import com.tourly.app.chat.domain.repository.ChatRepository
import com.tourly.app.core.network.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor() : ChatRepository {
    
    // Mocking real-time messages with a StateFlow for now
    private val _messages = MutableStateFlow<Map<Long, List<Message>>>(emptyMap())

    override fun getMessages(tourId: Long): Flow<List<Message>> = flow {
        // Initialize with empty list for this tour
        if (_messages.value[tourId] == null) {
            _messages.value += (tourId to emptyList())
        }
        
        _messages.collect { allMessages ->
            emit(allMessages[tourId] ?: emptyList())
        }
    }

    override suspend fun sendMessage(tourId: Long, content: String): Result<Unit> {
        delay(500) // Simulate network delay
        val currentMessages = _messages.value[tourId] ?: emptyList()
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = 1,
            senderName = "Me",
            senderImageUrl = null,
            content = content,
            timestamp = LocalDateTime.now(),
            isFromMe = true
        )
        _messages.value += (tourId to (currentMessages + newMessage))
        return Result.Success(Unit)
    }
}
