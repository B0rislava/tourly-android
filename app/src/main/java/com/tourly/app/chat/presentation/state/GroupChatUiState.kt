package com.tourly.app.chat.presentation.state

import com.tourly.app.chat.domain.model.Message

data class GroupChatUiState(
    val tourTitle: String = "",
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMessage: String = ""
)
