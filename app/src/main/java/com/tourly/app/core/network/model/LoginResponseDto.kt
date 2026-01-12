package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)
