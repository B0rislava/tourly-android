package com.tourly.app.home.domain.model

data class Tour(
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
    val rating: Double,
    val reviewsCount: Int,
    val meetingPoint: String?,
    val imageUrl: String?,
    val cancellationPolicy: String?,
    val whatsIncluded: String?,
    val guideBio: String?,
    val guideRating: Double,
    val guideToursCompleted: Int,
    val guideImageUrl: String?
)
