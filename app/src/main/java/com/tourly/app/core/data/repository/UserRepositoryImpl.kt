package com.tourly.app.core.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.UserDto
import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.NetworkResponseMapper
import kotlinx.coroutines.flow.map

import com.tourly.app.core.domain.model.User
import com.tourly.app.core.data.mapper.UserMapper
import kotlinx.coroutines.flow.Flow

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val client: HttpClient
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


    override suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<User> {
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
