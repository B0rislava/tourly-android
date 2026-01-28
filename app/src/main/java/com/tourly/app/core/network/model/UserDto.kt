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
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val followerCount: Int = 0,
    val certifications: String? = null,
    val toursCompleted: Int = 0,
    val isVerified: Boolean = false
)
