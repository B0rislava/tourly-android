package com.tourly.app.core.domain.repository

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Booking

interface BookingRepository {
    suspend fun bookTour(tourId: Long, numberOfParticipants: Int): Result<Booking>
    suspend fun getMyBookings(): Result<List<Booking>>
    suspend fun getGuideBookings(): Result<List<Booking>>
    suspend fun cancelBooking(id: Long): Result<Unit>
    suspend fun completeBooking(id: Long): Result<Unit>
}
