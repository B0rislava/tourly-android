package com.tourly.app.reviews.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Long,
    val reviewerName: String,
    val reviewerProfilePicture: String?,
    val tourRating: Int,
    val guideRating: Int,
    val comment: String?,
    val createdAt: String,
    val tourTitle: String? = null
)
