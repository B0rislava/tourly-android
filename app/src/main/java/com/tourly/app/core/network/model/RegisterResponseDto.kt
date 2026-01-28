package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val token: String? = null,
    val refreshToken: String? = null,
    val user: UserDto
)
