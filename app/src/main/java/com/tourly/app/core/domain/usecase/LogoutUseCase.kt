package com.tourly.app.core.domain.usecase

import com.tourly.app.chat.domain.repository.ChatRepository
import com.tourly.app.core.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke() {
        userRepository.logout()
        chatRepository.clear()
    }
}
