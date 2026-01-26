package com.tourly.app.create_tour.presentation.state

import com.tourly.app.home.domain.model.Tag

data class CreateTourUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val duration: String = "",
    val maxGroupSize: String = "",
    val pricePerPerson: String = "",
    val whatsIncluded: String = "",
    val scheduledDate: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val meetingPointAddress: String = "",
    val imageUri: android.net.Uri? = null,

    val availableTags: List<Tag> = emptyList(),
    val selectedTagIds: Set<Long> = emptySet(),

    val titleError: String? = null,
    val descriptionError: String? = null,
    val locationError: String? = null,
    val durationError: String? = null,
    val maxGroupSizeError: String? = null,
    val priceError: String? = null,
    val dateError: String? = null,
    val meetingPointAddressError: String? = null,

    val isLoading: Boolean = false,
    val createTourError: String? = null,
)