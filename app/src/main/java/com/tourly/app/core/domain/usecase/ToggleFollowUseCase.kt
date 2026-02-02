package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class ToggleFollowUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, shouldFollow: Boolean): Result<Unit> {
        return if (shouldFollow) {
            userRepository.followUser(userId)
        } else {
            userRepository.unfollowUser(userId)
        }
    }
}
