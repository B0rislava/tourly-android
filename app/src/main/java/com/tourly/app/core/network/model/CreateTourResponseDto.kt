package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTourResponseDto(
    val id: Long,
    val tourGuideId: Long,
    val guideName: String,
    val title: String,
    val description: String,
    val location: String,
    val duration: String,
    val maxGroupSize: Int,
    val pricePerPerson: Double,
    val scheduledDate: String,
    val createdAt: String,
    val status: String,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val meetingPoint: String? = null,
    val imageUrl: String? = null,
    val cancellationPolicy: String? = null,
    val whatsIncluded: String? = null,
    val guideBio: String? = null,
    val guideRating: Double = 0.0,
    val guideToursCompleted: Int = 0,
    val guideImageUrl: String? = null
)
