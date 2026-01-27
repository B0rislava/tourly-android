package com.tourly.app.notifications.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime,
    val type: String?,
    val relatedId: Long?
)
