package com.tourly.app.reviews.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    val bookingId: Long,
    val tourRating: Int,
    val guideRating: Int,
    val comment: String?
)
