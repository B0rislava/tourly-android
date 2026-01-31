package com.tourly.app.login.data.dto

import com.tourly.app.profile.data.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("token")
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)
