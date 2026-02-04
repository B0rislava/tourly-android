package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> {
        return userRepository.currentUserProfile
    }
}
