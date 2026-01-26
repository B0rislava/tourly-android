package com.tourly.app.core.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.repository.BookingRepository
import javax.inject.Inject

class CancelBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Long): Result<Unit> {
        return bookingRepository.cancelBooking(bookingId)
    }
}
