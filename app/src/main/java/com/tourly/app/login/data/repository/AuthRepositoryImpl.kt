package com.tourly.app.login.data.repository

import javax.inject.Inject
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.core.network.model.LoginRequestDto
import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.model.RegisterRequestDto
import com.tourly.app.core.network.model.RegisterResponseDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.NetworkResponseMapper


class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<LoginResponseDto> {
        println("AuthRepository: Attempting login for $email")
        val response = apiService.login(LoginRequestDto(email, password))
        val result = NetworkResponseMapper.map<LoginResponseDto>(response)

        if (result is Result.Success) {
            println("AuthRepository: Login successful, saving token (length: ${result.data.token.length})")
            tokenManager.saveTokens(result.data.token, result.data.refreshToken)
        } else if (result is Result.Error) {
             println("AuthRepository: Login failed - ${result.message} (Code: ${result.code})")
        }

        return result
    }

    override suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponseDto> {
        val request = RegisterRequestDto(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            role = role
        )
        val response = apiService.register(request)
        val result = NetworkResponseMapper.map<RegisterResponseDto>(response)

        return result
    }

    override suspend fun verifyCode(email: String, code: String): Result<LoginResponseDto> {
        val response = apiService.verifyCode(email, code)
        val result = NetworkResponseMapper.map<LoginResponseDto>(response)

        if (result is Result.Success) {
            println("AuthRepository: Verification successful, saving tokens...")
            tokenManager.saveTokens(result.data.token, result.data.refreshToken)
            println("AuthRepository: Tokens saved successfully")
        }

        return result
    }

    override suspend fun resendCode(email: String): Result<Unit> {
        val response = apiService.resendCode(email)
        return NetworkResponseMapper.map<Unit>(response)
    }

    override suspend fun googleSignIn(idToken: String, role: UserRole?): Result<LoginResponseDto> {
        val response = apiService.googleLogin(idToken, role?.name)
        val result = NetworkResponseMapper.map<LoginResponseDto>(response)

        if (result is Result.Success) {
            tokenManager.saveTokens(result.data.token, result.data.refreshToken)
        }

        return result
    }
}
