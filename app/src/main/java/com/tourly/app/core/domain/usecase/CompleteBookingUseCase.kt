package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.repository.BookingRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class CompleteBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Long): Result<Unit> {
        return bookingRepository.completeBooking(bookingId)
    }
}
