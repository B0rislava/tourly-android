package com.tourly.app.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookingResponseDto(
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
    val hasReview: Boolean,
    val customerName: String? = null,
    val customerImageUrl: String? = null
)
