package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.model.UserDto
import javax.inject.Inject
import com.tourly.app.core.network.Result

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<UserDto> {
        return repository.getUserProfile()
    }
}
