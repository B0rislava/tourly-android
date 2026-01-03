package com.tourly.app.core.domain.repository

import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.network.model.UserDto

interface UserRepository {
    suspend fun getUserProfile(token: String): Result<UserDto>
    suspend fun updateUserProfile(token: String, request: UpdateProfileRequestDto): Result<UserDto>
    suspend fun uploadProfilePicture(token: String, fileBytes: ByteArray): Result<UserDto>
}
