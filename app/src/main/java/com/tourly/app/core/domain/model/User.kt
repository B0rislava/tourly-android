package com.tourly.app.core.domain.model

import com.tourly.app.login.domain.UserRole

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val profilePictureUrl: String?,
    val bio: String? = null,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val certifications: String? = null,
    val toursCompleted: Int = 0,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
) {
    val fullName: String
        get() = "$firstName $lastName"
}
