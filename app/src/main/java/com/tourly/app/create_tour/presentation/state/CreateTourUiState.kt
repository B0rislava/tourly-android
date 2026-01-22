package com.tourly.app.create_tour.presentation.state



data class CreateTourUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val duration: String = "",
    val maxGroupSize: String = "",
    val pricePerPerson: String = "",
    val whatsIncluded: String = "",
    val scheduledDate: Long? = null,
    val imageUri: android.net.Uri? = null,

    val titleError: String? = null,
    val descriptionError: String? = null,
    val locationError: String? = null,
    val durationError: String? = null,
    val maxGroupSizeError: String? = null,
    val priceError: String? = null,
    val dateError: String? = null,

    val isLoading: Boolean = false,
    val createTourError: String? = null,
)