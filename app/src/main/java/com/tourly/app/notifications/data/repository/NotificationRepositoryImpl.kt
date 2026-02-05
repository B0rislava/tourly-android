package com.tourly.app.notifications.data.repository

import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.notifications.data.mapper.toDomain
import com.tourly.app.notifications.data.remote.NotificationApiService
import com.tourly.app.notifications.data.remote.dto.NotificationDto
import com.tourly.app.notifications.domain.model.Notification
import com.tourly.app.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApiService
) : NotificationRepository {
    override suspend fun getNotifications(): Result<List<Notification>> {
        return when (val result = NetworkResponseMapper.map<List<NotificationDto>> { api.getNotifications() }) {
            is Result.Success -> Result.Success(result.data.map { it.toDomain() })
            is Result.Error -> result
        }
    }

    override suspend fun getUnreadCount(): Result<Int> {
        return NetworkResponseMapper.map { api.getUnreadCount() }
    }

    override suspend fun markAsRead(id: Long): Result<Unit> {
        return NetworkResponseMapper.map { api.markAsRead(id) }
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        return NetworkResponseMapper.map { api.markAllAsRead() }
    }
}
