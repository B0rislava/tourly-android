package com.tourly.app.create_tour.domain.mapper

import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.create_tour.domain.model.CreateTourParams
import javax.inject.Inject

class CreateTourMapper @Inject constructor() {
    fun toDto(params: CreateTourParams): CreateTourRequestDto {
        return CreateTourRequestDto(
            title = params.title,
            description = params.description,
            location = params.location,
            duration = params.duration,
            maxGroupSize = params.maxGroupSize,
            pricePerPerson = params.pricePerPerson,
            whatsIncluded = params.whatsIncluded,
            scheduledDate = params.scheduledDate?.toString(),
            latitude = params.latitude,
            longitude = params.longitude,
            meetingPoint = params.meetingPoint,
            tagIds = params.tagIds
        )
    }
}