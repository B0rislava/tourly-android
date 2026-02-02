package com.tourly.app.home.data.mapper

import com.tourly.app.core.network.mapper.TagMapper
import com.tourly.app.home.data.dto.CreateTourResponseDto
import com.tourly.app.core.domain.model.Tour

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
            availableSpots = dto.availableSpots,
            pricePerPerson = dto.pricePerPerson,
            scheduledDate = dto.scheduledDate,
            startTime = dto.startTime,
            createdAt = dto.createdAt,
            status = dto.status,
            rating = dto.rating,
            reviewsCount = dto.reviewsCount,
            meetingPoint = dto.meetingPoint,
            imageUrl = dto.imageUrl,
            whatsIncluded = dto.whatsIncluded,
            guideBio = dto.guideBio,
            guideRating = dto.guideRating,
            guideImageUrl = dto.guideImageUrl,
            latitude = dto.latitude,
            longitude = dto.longitude,
            isSaved = dto.isSaved,
            tags = dto.tags.map { TagMapper.toDomain(it) }
        )
    }

    fun toDomainList(dtos: List<CreateTourResponseDto>): List<Tour> {
        return dtos.map { toDomain(it) }
    }
}
