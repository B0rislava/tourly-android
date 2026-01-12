package com.tourly.app.core.domain.repository

import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.network.model.UserDto

interface UserRepository {
    suspend fun getUserProfile(): Result<UserDto>
    suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<UserDto>
    suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<UserDto>
    suspend fun logout()
}
