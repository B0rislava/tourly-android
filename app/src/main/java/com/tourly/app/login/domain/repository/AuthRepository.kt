package com.tourly.app.login.domain.repository

import com.tourly.app.core.network.model.LoginResponse
import com.tourly.app.core.network.model.RegisterResponse
import com.tourly.app.login.domain.UserRole

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<LoginResponse>
    suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<RegisterResponse>
}
