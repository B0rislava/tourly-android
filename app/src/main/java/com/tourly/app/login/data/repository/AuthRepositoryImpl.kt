package com.tourly.app.login.data.repository

import javax.inject.Inject
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.ErrorResponse
import com.tourly.app.core.network.model.LoginRequestDto
import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.model.RegisterRequestDto
import com.tourly.app.core.network.model.RegisterResponseDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.http.isSuccess
import io.ktor.client.call.body


class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<LoginResponseDto> {
        return try {
            println("AuthRepository: Attempting login for $email")
            val response = apiService.login(LoginRequestDto(email, password))

            if (response.status.isSuccess()) {
                val loginResponse = response.body<LoginResponseDto>()
                println("AuthRepository: Login successful, saving token (length: ${loginResponse.token.length})")

                // Save the token and verify it was saved
                tokenManager.saveTokens(loginResponse.token, loginResponse.refreshToken)

                val savedToken = tokenManager.getToken()
                if (savedToken == null) {
                    println("AuthRepository: ERROR - Token verification failed after save!")
                    return Result.failure(Exception("Failed to save authentication token"))
                }

                println("AuthRepository: Token verified successfully, returning user data")
                Result.success(loginResponse)
            } else {
                val errorResponse = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }

                val errorMessage = errorResponse?.description ?: errorResponse?.message ?: "Login failed: ${response.status}"
                println("AuthRepository: Login failed - $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            println("AuthRepository: Login exception - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponseDto> {
        return try {
            val request = RegisterRequestDto(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password,
                role = role
            )
            val response = apiService.register(request)
            if (response.status.isSuccess()) {
                val registerResponse = response.body<RegisterResponseDto>()
                tokenManager.saveToken(registerResponse.token)
                tokenManager.saveRefreshToken(registerResponse.refreshToken)
                Result.success(registerResponse)
            } else {
                val errorResponse = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }
                
                val errorMessage = errorResponse?.description ?: errorResponse?.message ?: "Registration failed: ${response.status}"
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
