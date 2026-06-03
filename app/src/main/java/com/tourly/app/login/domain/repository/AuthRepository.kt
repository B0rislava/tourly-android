package com.tourly.app.login.domain.repository

import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.User

interface AuthRepository {
    suspend fun signIn(
        email: String,
        password: String
    ): Result<User>

    suspend fun signUp(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: UserRole
    ): Result<User>

    suspend fun verifyCode(
        email: String,
        code: String
    ): Result<User>

    suspend fun resendCode(email: String): Result<Unit>
    
    suspend fun googleSignIn(idToken: String, role: UserRole? = null): Result<User>

    suspend fun forgotPassword(email: String): Result<Unit>

    suspend fun verifyResetCode(email: String, code: String): Result<Unit>

    suspend fun resetPassword(email: String, resetCode: String, newPassword: String): Result<Unit>
}
