package com.tourly.app.chat.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val id: Long? = null,
    val tourId: Long,
    val senderId: Long,
    val senderName: String,
    val content: String,
    val timestamp: String // ISO LocalDateTime string
)
