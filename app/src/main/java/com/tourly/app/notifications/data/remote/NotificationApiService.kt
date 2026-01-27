package com.tourly.app.notifications.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class NotificationApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getNotifications(): HttpResponse {
        return client.get("notifications")
    }

    suspend fun getUnreadCount(): HttpResponse {
        return client.get("notifications/unread-count")
    }

    suspend fun markAsRead(id: Long): HttpResponse {
        return client.post("notifications/$id/read")
    }

    suspend fun markAllAsRead(): HttpResponse {
        return client.post("notifications/read-all")
    }
}
