package com.tourly.app.core.data.mapper

import com.tourly.app.core.domain.model.User
import com.tourly.app.profile.data.dto.UserDto

object UserMapper {
    fun mapToDomain(dto: UserDto): User {
        return User(
            id = dto.id ?: -1L,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            role = dto.role,
            profilePictureUrl = dto.profilePictureUrl,
            bio = dto.bio,
            rating = dto.rating,
            reviewsCount = dto.reviewsCount,
            followerCount = dto.followerCount,
            followingCount = dto.followingCount,
            certifications = dto.certifications,
            toursCompleted = dto.toursCompleted,
            isVerified = dto.isVerified,
            isFollowing = dto.isFollowing
        )
    }
}
