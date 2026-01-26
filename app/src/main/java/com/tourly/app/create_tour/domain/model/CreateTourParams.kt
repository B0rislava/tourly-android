package com.tourly.app.create_tour.domain.model

import android.net.Uri
import java.time.LocalDate

data class CreateTourParams(
    val title: String,
    val description: String,
    val location: String,
    val duration: String,
    val maxGroupSize: Int,
    val pricePerPerson: Double,
    val whatsIncluded: String?,
    val scheduledDate: LocalDate?,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val meetingPoint: String? = null,
    val imageUri: Uri? = null,
    val tagIds: List<Long> = emptyList()
)
