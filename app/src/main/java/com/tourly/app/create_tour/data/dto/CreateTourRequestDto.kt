package com.tourly.app.create_tour.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateTourRequestDto(
    val title: String,
    val description: String,
    val location: String,
    val duration: String,
    val maxGroupSize: Int,
    val pricePerPerson: Double,
    val whatsIncluded: String?,
    val scheduledDate: String?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val meetingPoint: String? = null,
    val tagIds: List<Long> = emptyList()
)