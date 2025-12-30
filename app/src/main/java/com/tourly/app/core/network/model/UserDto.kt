package com.tourly.app.core.network.model

import com.tourly.app.login.domain.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Long? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val profilePictureUrl: String? = null
)
