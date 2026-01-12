package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)
