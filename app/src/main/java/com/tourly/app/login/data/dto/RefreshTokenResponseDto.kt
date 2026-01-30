package com.tourly.app.login.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)
