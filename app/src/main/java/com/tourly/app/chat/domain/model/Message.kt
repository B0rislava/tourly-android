package com.tourly.app.chat.domain.model

import java.time.LocalDateTime

data class Message(
    val id: String,
    val senderId: Long,
    val senderName: String,
    val content: String,
    val timestamp: LocalDateTime,
    val isFromMe: Boolean,
    val isGuide: Boolean = false
)
