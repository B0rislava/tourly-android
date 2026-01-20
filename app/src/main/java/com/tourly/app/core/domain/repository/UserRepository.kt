package com.tourly.app.core.domain.repository

import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.network.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<User>
    suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<User>
    suspend fun logout()
    fun getTokenFlow(): Flow<Boolean>
}
