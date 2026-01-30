package com.tourly.app.create_tour.data.repository

import android.content.Context
import androidx.core.net.toUri
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.create_tour.data.dto.CreateTourRequestDto
import com.tourly.app.home.data.dto.CreateTourResponseDto
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.core.domain.model.Tour
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val apiService: TourApiService,
    @ApplicationContext private val context: Context
) : TourRepository {

    override suspend fun createTour(
        request: CreateTourRequestDto,
        imageUri: String?
    ): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto>(
            apiService.createTour(context, request, imageUri?.toUri())
        )) {
            is Result.Success -> Result.Success(TourMapper.toDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getMyTours(): Result<List<Tour>> {
        return when (val result = NetworkResponseMapper.map<List<CreateTourResponseDto>>(apiService.getMyTours())) {
            is Result.Success -> Result.Success(TourMapper.toDomainList(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun updateTour(
        id: Long,
        request: CreateTourRequestDto,
        imageUri: String?
    ): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto>(
            apiService.updateTour(context, id, request, imageUri?.toUri())
        )) {
            is Result.Success -> Result.Success(TourMapper.toDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun deleteTour(id: Long): Result<Unit> {
        return NetworkResponseMapper.map(apiService.deleteTour(id))
    }
}
