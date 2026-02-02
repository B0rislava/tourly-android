package com.tourly.app.chat.presentation.model

data class ChatItem(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    val role: String
)
