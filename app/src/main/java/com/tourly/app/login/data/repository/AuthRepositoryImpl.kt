package com.tourly.app.login.data.repository

import javax.inject.Inject
import com.tourly.app.core.network.api.AuthApiService
import com.tourly.app.login.data.dto.LoginRequestDto
import com.tourly.app.login.data.dto.LoginResponseDto
import com.tourly.app.login.data.dto.RegisterRequestDto
import com.tourly.app.login.data.dto.RegisterResponseDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.login.domain.repository.AuthRepository
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.data.mapper.UserMapper


class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        println("AuthRepository: Attempting login for $email")
        return when (val result = NetworkResponseMapper.map<LoginResponseDto> { apiService.login(LoginRequestDto(email, password)) }) {
            is Result.Success -> {
                println("AuthRepository: Login successful, saving token (length: ${result.data.accessToken.length})")
                tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(UserMapper.mapToDomain(result.data.user))
            }
            is Result.Error -> {
                println("AuthRepository: Login failed - ${result.message} (Code: ${result.code})")
                result
            }
        }
    }

    override suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<User> {
        val request = RegisterRequestDto(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            role = role
        )
        return when (val result = NetworkResponseMapper.map<RegisterResponseDto> { apiService.register(request) }) {
            is Result.Success -> {
                if (!result.data.accessToken.isNullOrEmpty() && !result.data.refreshToken.isNullOrEmpty()) {
                    tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                }
                Result.Success(UserMapper.mapToDomain(result.data.user))
            }
            is Result.Error -> result
        }
    }

    override suspend fun verifyCode(email: String, code: String): Result<User> {
        return when (val result = NetworkResponseMapper.map<LoginResponseDto> { apiService.verifyCode(email, code) }) {
            is Result.Success -> {
                println("AuthRepository: Verification successful, saving tokens...")
                tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                println("AuthRepository: Tokens saved successfully")
                Result.Success(UserMapper.mapToDomain(result.data.user))
            }
            is Result.Error -> result
        }
    }

    override suspend fun resendCode(email: String): Result<Unit> {
        return NetworkResponseMapper.map { apiService.resendCode(email) }
    }

    override suspend fun googleSignIn(idToken: String, role: UserRole?): Result<User> {
        return when (val result = NetworkResponseMapper.map<LoginResponseDto> { apiService.googleLogin(idToken, role?.name) }) {
            is Result.Success -> {
                tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                Result.Success(UserMapper.mapToDomain(result.data.user))
            }
            is Result.Error -> result
        }
    }
}
