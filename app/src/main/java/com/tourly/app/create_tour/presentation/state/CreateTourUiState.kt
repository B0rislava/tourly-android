package com.tourly.app.create_tour.presentation.state

import android.net.Uri
import com.tourly.app.core.domain.model.Tag
import java.time.LocalTime

data class CreateTourUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val duration: String = "",
    val maxGroupSize: String = "",
    val pricePerPerson: String = "",
    val whatsIncluded: String = "",
    val scheduledDate: Long? = null,
    val startTime: LocalTime? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val meetingPointAddress: String = "",
    val imageUri: Uri? = null,

    val availableTags: List<Tag> = emptyList(),
    val selectedTagIds: Set<Long> = emptySet(),

    val titleError: String? = null,
    val descriptionError: String? = null,
    val locationError: String? = null,
    val durationError: String? = null,
    val maxGroupSizeError: String? = null,
    val priceError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val meetingPointAddressError: String? = null,

    val isLoading: Boolean = false,
    val createTourError: String? = null,
)