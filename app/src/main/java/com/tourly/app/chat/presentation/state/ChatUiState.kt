package com.tourly.app.chat.presentation.state

import com.tourly.app.chat.presentation.model.ChatItem

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val chats: List<ChatItem>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}
