package com.tourly.app.core.network.model

import com.tourly.app.login.domain.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val email: String,
    val role: UserRole
)

@Serializable
data class RegisterRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val role: UserRole
)

@Serializable
data class RegisterResponse(
    val message: String,
    val email: String
)
