package com.tourly.app.chat.domain.repository

import com.tourly.app.chat.domain.model.Message
import com.tourly.app.core.network.Result
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(tourId: Long): Flow<List<Message>>
    suspend fun sendMessage(tourId: Long, content: String): Result<Unit>
    fun clear()
}
