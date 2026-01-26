package com.tourly.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BookTourRequestDto(
    val tourId: Long,
    val numberOfParticipants: Int = 1
)
