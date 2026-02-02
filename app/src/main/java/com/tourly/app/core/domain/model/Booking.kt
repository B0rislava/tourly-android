package com.tourly.app.core.domain.model

data class Booking(
    val id: Long,
    val tourId: Long,
    val tourTitle: String,
    val tourLocation: String,
    val tourImageUrl: String?,
    val tourScheduledDate: String?,
    val numberOfParticipants: Int,
    val bookingDate: String,
    val status: String,
    val pricePerPerson: Double,
    val totalPrice: Double,
    val hasReview: Boolean = false,
    val customerName: String? = null,
    val customerImageUrl: String? = null
)
