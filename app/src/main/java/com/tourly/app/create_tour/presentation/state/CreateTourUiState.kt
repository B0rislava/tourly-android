package com.tourly.app.create_tour.presentation.state

import com.tourly.app.create_tour.domain.TourType

data class CreateTourUiState(
    val type: TourType = TourType.SINGLE_DAY,
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val duration: String = "",
    val pricePerPerson: Double = 0.0,
    val maxGroupSize: Int = 0,
    val days: List<DayProgramme>? = null,
    val whatsIncluded: String = "",
    // TODO: Add property for images

    val isLoading: Boolean = false,
    val createTourError: String? = null,
    val validationErrors: Map<String, String>? = null

)