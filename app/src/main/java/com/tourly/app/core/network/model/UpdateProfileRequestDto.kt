package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String? = null
)
