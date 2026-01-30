package com.tourly.app.login.data.dto

import com.tourly.app.profile.data.dto.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    val token: String? = null,
    val refreshToken: String? = null,
    val user: UserDto
)
