package com.tourly.app.profile.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val bio: String? = null,
    val certifications: String? = null,
    val password: String? = null
)
