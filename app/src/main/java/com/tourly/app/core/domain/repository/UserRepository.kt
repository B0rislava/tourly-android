package com.tourly.app.core.domain.repository

import com.tourly.app.core.domain.model.User
import com.tourly.app.profile.data.dto.UpdateProfileRequestDto
import com.tourly.app.core.network.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun getUserProfile(userId: Long): Result<User>
    suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<User>
    suspend fun uploadProfilePicture(uriString: String): Result<User>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun logout()
    fun getTokenFlow(): Flow<Boolean>
}
