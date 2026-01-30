package com.tourly.app.login.data.dto

import com.tourly.app.profile.data.dto.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)
