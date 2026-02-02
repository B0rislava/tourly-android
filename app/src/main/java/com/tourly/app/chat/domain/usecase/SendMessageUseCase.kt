package com.tourly.app.chat.domain.usecase

import com.tourly.app.chat.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(tourId: Long, content: String) = repository.sendMessage(tourId, content)
}
