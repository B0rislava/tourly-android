package com.tourly.app.home.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.repository.BookingRepository
import javax.inject.Inject

class BookTourUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(tourId: Long, numberOfParticipants: Int): Result<Booking> {
        return bookingRepository.bookTour(tourId, numberOfParticipants)
    }
}
