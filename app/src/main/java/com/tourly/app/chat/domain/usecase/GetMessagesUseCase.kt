package com.tourly.app.chat.domain.usecase

import com.tourly.app.chat.domain.repository.ChatRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(tourId: Long) = repository.getMessages(tourId)
}
