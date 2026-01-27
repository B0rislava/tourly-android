package com.tourly.app.notifications.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String,
    val type: String?,
    val relatedId: Long?
)
