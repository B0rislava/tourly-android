package com.tourly.app.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.chat.presentation.model.ChatItem
import com.tourly.app.chat.presentation.state.ChatUiState
import com.tourly.app.core.domain.usecase.GetMyBookingsUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.usecase.GetMyToursUseCase
import com.tourly.app.login.domain.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val getMyToursUseCase: GetMyToursUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadChats()
    }

    fun loadChats() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            
            val userResult = getUserProfileUseCase()
            if (userResult is Result.Error) {
                _uiState.value = ChatUiState.Error(userResult.message)
                return@launch
            }

            val user = (userResult as Result.Success).data
            
            if (user.role == UserRole.TRAVELER) {
                fetchTravelerChats()
            } else {
                fetchGuideChats()
            }
        }
    }

    private suspend fun fetchTravelerChats() {
        when (val result = getMyBookingsUseCase()) {
            is Result.Success -> {
                val chats = result.data.map { 
                    ChatItem(
                        id = it.tourId,
                        title = it.tourTitle,
                        imageUrl = it.tourImageUrl,
                        role = "Member"
                    )
                }.distinctBy { it.id }
                _uiState.value = ChatUiState.Success(chats)
            }
            is Result.Error -> {
                _uiState.value = ChatUiState.Error(result.message)
            }
        }
    }

    private suspend fun fetchGuideChats() {
        when (val result = getMyToursUseCase()) {
            is Result.Success -> {
                val chats = result.data.map { 
                    ChatItem(
                        id = it.id,
                        title = it.title,
                        imageUrl = it.imageUrl,
                        role = "Guide"
                    )
                }.distinctBy { it.id }
                _uiState.value = ChatUiState.Success(chats)
            }
            is Result.Error -> {
                _uiState.value = ChatUiState.Error(result.message)
            }
        }
    }
}
