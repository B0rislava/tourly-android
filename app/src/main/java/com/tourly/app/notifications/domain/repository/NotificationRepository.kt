package com.tourly.app.notifications.domain.repository

import com.tourly.app.core.network.Result
import com.tourly.app.notifications.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun getUnreadCount(): Result<Int>
    suspend fun markAsRead(id: Long): Result<Unit>
    suspend fun markAllAsRead(): Result<Unit>
}
