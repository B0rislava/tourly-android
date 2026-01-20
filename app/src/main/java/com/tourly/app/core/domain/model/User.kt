package com.tourly.app.core.domain.model

import com.tourly.app.login.domain.UserRole

data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val profilePictureUrl: String?
) {
    val fullName: String
        get() = "$firstName $lastName"
}
