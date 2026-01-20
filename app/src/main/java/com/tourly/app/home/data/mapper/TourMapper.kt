package com.tourly.app.home.data.mapper

import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.home.domain.model.Tour

object TourMapper {
    fun toDomain(dto: CreateTourResponseDto): Tour {
        return Tour(
            id = dto.id,
            tourGuideId = dto.tourGuideId,
            guideName = dto.guideName,
            title = dto.title,
            description = dto.description,
            location = dto.location,
            duration = dto.duration,
            maxGroupSize = dto.maxGroupSize,
            pricePerPerson = dto.pricePerPerson,
            scheduledDate = dto.scheduledDate,
            createdAt = dto.createdAt,
            status = dto.status,
            rating = dto.rating,
            reviewsCount = dto.reviewsCount,
            meetingPoint = dto.meetingPoint,
            imageUrl = dto.imageUrl,
            cancellationPolicy = dto.cancellationPolicy,
            whatsIncluded = dto.whatsIncluded,
            guideBio = dto.guideBio,
            guideRating = dto.guideRating,
            guideToursCompleted = dto.guideToursCompleted,
            guideImageUrl = dto.guideImageUrl
        )
    }

    fun toDomainList(dtos: List<CreateTourResponseDto>): List<Tour> {
        return dtos.map { toDomain(it) }
    }
}
