package com.tourly.app.home.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookTourRequestDto(
    val tourId: Long,
    val numberOfParticipants: Int = 1
)
