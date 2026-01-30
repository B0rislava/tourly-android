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

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val client: HttpClient,
    @ApplicationContext private val context: Context
) : UserRepository {

    override suspend fun getUserProfile(): Result<User> {
        return try {
            val response = authApiService.getProfile()
            when (val result = NetworkResponseMapper.map<UserDto>(response)) {
                 is Result.Success -> Result.Success(UserMapper.mapToDomain(result.data))
                 is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<User> {
        return try {
            val response = authApiService.updateProfile(request)
            when (val result = NetworkResponseMapper.map<LoginResponseDto>(response)) {
                is Result.Success -> {
                    tokenManager.saveToken(result.data.token)
                    Result.Success(UserMapper.mapToDomain(result.data.user))
                }
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }


    private suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<User> {
        return try {
            val response = authApiService.uploadProfilePicture(fileBytes)
            when (val result = NetworkResponseMapper.map<UserDto>(response)) {
                is Result.Success -> Result.Success(UserMapper.mapToDomain(result.data))
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
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
        return try {
            val response = authApiService.deleteProfile()
            when (val result = NetworkResponseMapper.map<Unit>(response)) {
                is Result.Success -> {
                    logout()
                    Result.Success(Unit)
                }
                is Result.Error -> result
            }
        } catch (e: Exception) {
            Result.Error(code = e.javaClass.simpleName, message = e.message ?: "Unknown error")
        }
    }

    override suspend fun logout() {
        println("UserRepository: Starting logout process...")

        // Clear token from storage
        tokenManager.clearToken()
        tokenManager.clearRefreshToken()
        client.authProvider<BearerAuthProvider>()?.clearToken()
        println("UserRepository: Token cleared via Standard Ktor Auth Clear - logout complete")
    }
    
    override fun getTokenFlow(): Flow<Boolean> {
        return tokenManager.getTokenFlow().map { it != null }
    }
}
