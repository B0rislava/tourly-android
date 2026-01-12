package com.tourly.app.login.domain.repository

import com.tourly.app.core.network.model.LoginResponseDto
import com.tourly.app.core.network.model.RegisterResponseDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.network.Result

interface AuthRepository {
    suspend fun signIn(
        email: String,
        password: String
    ): Result<LoginResponseDto>

    suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponseDto>
}
