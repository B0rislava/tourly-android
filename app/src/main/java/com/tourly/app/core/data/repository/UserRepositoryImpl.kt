package com.tourly.app.core.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.tourly.app.core.network.util.APIException
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.UserDto
import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.http.isSuccess
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val client: HttpClient
) : UserRepository {

    override suspend fun getUserProfile(): Result<UserDto> {
        return try {
            val response = authApiService.getProfile()
            if (response.status.isSuccess()) {
                val user = response.body<UserDto>()
                Result.success(user)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(APIException(response.status.value, "Profile fetch failed: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(request: UpdateProfileRequestDto): Result<UserDto> {
        return try {
            val response = authApiService.updateProfile(request)
            if (response.status.isSuccess()) {
                val loginResponse = response.body<LoginResponseDto>()
                tokenManager.saveToken(loginResponse.token)
                Result.success(loginResponse.user)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(
                    APIException(
                        response.status.value,
                        "Profile update failed: $errorBody"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun uploadProfilePicture(fileBytes: ByteArray): Result<UserDto> {
        return try {
            val response = authApiService.uploadProfilePicture(fileBytes)
            if (response.status.isSuccess()) {
                val user = response.body<UserDto>()
                Result.success(user)
            } else {
                val errorBody = response.bodyAsText()
                Result.failure(APIException(response.status.value, "Profile picture upload failed: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
}
