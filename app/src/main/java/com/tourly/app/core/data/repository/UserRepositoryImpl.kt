package com.tourly.app.core.data.repository

import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.network.model.UserDto
import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.util.APIException
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : UserRepository {

    override suspend fun getUserProfile(token: String): Result<UserDto> {
        return try {
            val response = authApiService.getProfile(token)
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

    override suspend fun updateUserProfile(token: String, request: UpdateProfileRequestDto): Result<UserDto> {
        return try {
            val response = authApiService.updateProfile(token, request)
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


    override suspend fun uploadProfilePicture(token: String, fileBytes: ByteArray): Result<UserDto> {
        return try {
            val response = authApiService.uploadProfilePicture(token, fileBytes)
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
}
