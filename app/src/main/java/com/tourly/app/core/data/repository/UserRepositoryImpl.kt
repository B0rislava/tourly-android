package com.tourly.app.core.data.repository

import android.content.Context
import androidx.core.net.toUri
import javax.inject.Inject
import javax.inject.Singleton
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.profile.data.dto.UserDto
import com.tourly.app.login.data.dto.LoginResponseDto
import com.tourly.app.profile.data.dto.UpdateProfileRequestDto
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.NetworkResponseMapper
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext

import com.tourly.app.core.domain.model.User
import com.tourly.app.core.data.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val client: HttpClient,
    @param:ApplicationContext private val context: Context
) : UserRepository {

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    override val currentUserProfile: Flow<User?> = _currentUserProfile.asStateFlow()

    override suspend fun getUserProfile(): Result<User> {
        return when (val result = NetworkResponseMapper.map<UserDto> { authApiService.getProfile() }) {
            is Result.Success -> {
                val user = UserMapper.mapToDomain(result.data)
                _currentUserProfile.value = user
                Result.Success(user)
            }
            is Result.Error -> result
        }
    }

    override suspend fun getUserProfile(userId: Long): Result<User> {
        return when (val result = NetworkResponseMapper.map<UserDto> { authApiService.getProfileById(userId) }) {
            is Result.Success -> Result.Success(UserMapper.mapToDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<User> {
        return when (val result = NetworkResponseMapper.map<LoginResponseDto> { authApiService.updateProfile(request) }) {
            is Result.Success -> {
                tokenManager.saveToken(result.data.accessToken)
                val user = UserMapper.mapToDomain(result.data.user)
                _currentUserProfile.value = user
                Result.Success(user)
            }
            is Result.Error -> result
        }
    }


    private suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<User> {
        return when (val result = NetworkResponseMapper.map<UserDto> { authApiService.uploadProfilePicture(fileBytes) }) {
            is Result.Success -> {
                val user = UserMapper.mapToDomain(result.data)
                _currentUserProfile.value = user
                Result.Success(user)
            }
            is Result.Error -> result
        }
    }

    override suspend fun uploadProfilePicture(uriString: String): Result<User> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uriString.toUri())
            val bytes = inputStream?.use { it.readBytes() }
            
            if (bytes != null) {
                uploadProfilePicture(bytes)
            } else {
                Result.Error(code = "FILE_ERROR", message = "Failed to read image file")
            }
        } catch (e: Exception) {
            Result.Error(code = "FILE_ERROR", message = e.message ?: "Error processing image")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return when (val result = NetworkResponseMapper.map<Unit> { authApiService.deleteProfile() }) {
            is Result.Success -> {
                logout()
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }

    override suspend fun followUser(userId: Long): Result<Unit> {
        return NetworkResponseMapper.map { authApiService.followUser(userId) }
    }

    override suspend fun unfollowUser(userId: Long): Result<Unit> {
        return NetworkResponseMapper.map { authApiService.unfollowUser(userId) }
    }

    override suspend fun logout() {
        println("UserRepository: Starting logout process...")

        // Clear token from storage
        tokenManager.clearToken()
        tokenManager.clearRefreshToken()
        client.authProvider<BearerAuthProvider>()?.clearToken()

        // Clear local cache
        _currentUserProfile.value = null
        println("UserRepository: Token cleared via Standard Ktor Auth Clear - logout complete")
    }
    
    override fun getTokenFlow(): Flow<Boolean> {
        return tokenManager.getTokenFlow().map { it != null }
    }
}
