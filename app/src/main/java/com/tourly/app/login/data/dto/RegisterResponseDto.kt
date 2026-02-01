package com.tourly.app.login.data.dto

import com.tourly.app.profile.data.dto.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseDto(
    @SerialName("token")
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val user: UserDto
)
