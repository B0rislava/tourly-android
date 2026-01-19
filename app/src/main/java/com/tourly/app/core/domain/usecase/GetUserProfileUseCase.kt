package com.tourly.app.core.domain.usecase

import javax.inject.Inject
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.Result

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getUserProfile()
    }
}
