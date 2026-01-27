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
        return try {
            val response = api.getNotifications()
            when (val result = NetworkResponseMapper.map<List<NotificationDto>>(response)) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getUnreadCount(): Result<Int> {
        return try {
            val response = api.getUnreadCount()
            NetworkResponseMapper.map<Int>(response)
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun markAsRead(id: Long): Result<Unit> {
        return try {
            val response = api.markAsRead(id)
            NetworkResponseMapper.map<Unit>(response)
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun markAllAsRead(): Result<Unit> {
        return try {
            val response = api.markAllAsRead()
            NetworkResponseMapper.map<Unit>(response)
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }
}
