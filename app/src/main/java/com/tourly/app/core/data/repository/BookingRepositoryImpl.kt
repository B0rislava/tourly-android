package com.tourly.app.core.data.repository

import com.tourly.app.core.mapper.BookingMapper
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.BookingApiService
import com.tourly.app.home.data.dto.BookTourRequestDto
import com.tourly.app.home.data.dto.BookingResponseDto
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService,
    private val mapper: BookingMapper
) : BookingRepository {

    override suspend fun bookTour(tourId: Long, numberOfParticipants: Int): Result<Booking> {
        val request = BookTourRequestDto(tourId, numberOfParticipants)
        return when (val result = NetworkResponseMapper.map<BookingResponseDto>(apiService.bookTour(request))) {
            is Result.Success -> Result.Success(mapper.toDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getMyBookings(): Result<List<Booking>> {
        return when (val result = NetworkResponseMapper.map<List<BookingResponseDto>>(apiService.getMyBookings())) {
            is Result.Success -> Result.Success(result.data.map { mapper.toDomain(it) })
            is Result.Error -> result
        }
    }

    override suspend fun cancelBooking(id: Long): Result<Unit> {
        return when (val result = NetworkResponseMapper.map<Unit>(apiService.cancelBooking(id))) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }

    override suspend fun completeBooking(id: Long): Result<Unit> {
        return when (val result = NetworkResponseMapper.map<Unit>(apiService.completeBooking(id))) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }
}
