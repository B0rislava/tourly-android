package com.tourly.app.login.data.repository

import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.ErrorResponse
import com.tourly.app.core.network.model.LoginRequest
import com.tourly.app.core.network.model.LoginResponse
import com.tourly.app.core.network.model.RegisterRequest
import com.tourly.app.core.network.model.RegisterResponse
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.storage.TokenManager
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.status.isSuccess()) {
                val loginResponse = response.body<LoginResponse>()
                tokenManager.saveToken(loginResponse.token)
                Result.success(loginResponse)
            } else {
                val errorResponse = try {
                    response.body<ErrorResponse>()
                } catch (e: Exception) {
                    null
                }
                
                val errorMessage = errorResponse?.description ?: errorResponse?.message ?: "Login failed: ${response.status}"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password,
                role = role
            )
            val response = apiService.register(request)
            if (response.status.isSuccess()) {
                Result.success(response.body())
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
