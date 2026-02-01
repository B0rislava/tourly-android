package com.tourly.app.core.domain.usecase

import javax.inject.Inject
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.Result

class GetUserProfileByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<User> {
        return repository.getUserProfile(userId)
    }
}
