package com.tourly.app.login.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequestDto(
    val email: String,
    val resetCode: String,
    val newPassword: String
)
