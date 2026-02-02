package com.tourly.app.core.domain.model

import java.time.LocalDateTime

data class Review(
    val id: Long,
    val bookingId: Long?,
    val reviewerName: String,
    val reviewerProfilePicture: String?,
    val tourRating: Int,
    val guideRating: Int,
    val comment: String?,
    val createdAt: LocalDateTime
)
