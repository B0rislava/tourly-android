package com.tourly.app.core.data.mapper

import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.model.UserDto

object UserMapper {
    fun mapToDomain(dto: UserDto): User {
        return User(
            id = dto.id ?: -1L, // Should not happen for authenticated user
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = dto.role,
            profilePictureUrl = dto.profilePictureUrl
        )
    }
}
