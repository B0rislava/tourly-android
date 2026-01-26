package com.tourly.app.core.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.repository.BookingRepository
import javax.inject.Inject

class GetMyBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(): Result<List<Booking>> {
        return bookingRepository.getMyBookings()
    }
}
