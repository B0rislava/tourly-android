package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTourResponseDto(
    val id: Long,
    val tourGuideId: Long,
    val title: String,
    val description: String,
    val location: String,
    val duration: String,
    val pricePerPerson: Double,
    val scheduledDate: String,
    val createdAt: String,
    val status: String
)
