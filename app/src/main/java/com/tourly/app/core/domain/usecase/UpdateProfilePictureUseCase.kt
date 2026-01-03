package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.model.UserDto
import javax.inject.Inject

class UpdateProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(token: String, fileBytes: ByteArray): Result<UserDto> {
        return userRepository.uploadProfilePicture(token, fileBytes)
    }
}
