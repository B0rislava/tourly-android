package com.tourly.app.core.network.model

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
    val tagIds: List<Long> = emptyList()
)
