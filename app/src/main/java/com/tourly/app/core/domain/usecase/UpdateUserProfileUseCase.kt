package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.network.model.UserDto
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(request: UpdateProfileRequestDto): Result<UserDto> {
        return userRepository.updateUserProfile(request)
    }
}
