package com.tourly.app.core.mapper

import com.tourly.app.core.domain.model.Booking
import com.tourly.app.home.data.dto.BookingResponseDto
import javax.inject.Inject

class BookingMapper @Inject constructor() {
    fun toDomain(dto: BookingResponseDto): Booking {
        return Booking(
            id = dto.id,
            tourId = dto.tourId,
            tourTitle = dto.tourTitle,
            tourLocation = dto.tourLocation,
            tourImageUrl = dto.tourImageUrl,
            tourScheduledDate = dto.tourScheduledDate,
            numberOfParticipants = dto.numberOfParticipants,
            bookingDate = dto.bookingDate,
            status = dto.status,
            pricePerPerson = dto.pricePerPerson,
            totalPrice = dto.totalPrice,
            hasReview = dto.hasReview,
            customerName = dto.customerName,
            customerImageUrl = dto.customerImageUrl
        )
    }
}
