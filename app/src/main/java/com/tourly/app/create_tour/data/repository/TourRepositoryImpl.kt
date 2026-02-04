package com.tourly.app.create_tour.data.repository

import android.content.Context
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.home.data.dto.CreateTourResponseDto
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.create_tour.data.mapper.CreateTourMapper
import com.tourly.app.create_tour.domain.model.CreateTourParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val apiService: TourApiService,
    @param:ApplicationContext private val context: Context,
    private val mapper: CreateTourMapper
) : TourRepository {

    override suspend fun createTour(params: CreateTourParams): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto> {
            apiService.createTour(context, mapper.toDto(params), params.imageUri)
        }) {
            is Result.Success -> Result.Success(TourMapper.toDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getMyTours(): Result<List<Tour>> {
        return when (val result = NetworkResponseMapper.map<List<CreateTourResponseDto>> { apiService.getMyTours() }) {
            is Result.Success -> Result.Success(TourMapper.toDomainList(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getToursByGuideId(guideId: Long): Result<List<Tour>> {
        return when (val result = NetworkResponseMapper.map<List<CreateTourResponseDto>> { apiService.getToursByGuideId(guideId) }) {
            is Result.Success -> Result.Success(TourMapper.toDomainList(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun updateTour(id: Long, params: CreateTourParams): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto> {
            apiService.updateTour(context, id, mapper.toDto(params), params.imageUri)
        }) {
            is Result.Success -> Result.Success(TourMapper.toDomain(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun deleteTour(id: Long): Result<Unit> {
        return NetworkResponseMapper.map { apiService.deleteTour(id) }
    }
}
