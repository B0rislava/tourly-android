package com.tourly.app.create_tour.data.repository

import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result as NetworkResult
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.home.domain.model.Tour
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val apiService: TourApiService
) : TourRepository {

    override suspend fun createTour(request: CreateTourRequestDto): Result<Tour> {
        return try {
            val result = NetworkResponseMapper.map<CreateTourResponseDto>(apiService.createTour(request))
            
            when (result) {
                is NetworkResult.Success -> Result.success(TourMapper.toDomain(result.data))
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyTours(): Result<List<Tour>> {
        return try {
            val result = NetworkResponseMapper.map<List<CreateTourResponseDto>>(apiService.getMyTours())

            when (result) {
                is NetworkResult.Success -> Result.success(TourMapper.toDomainList(result.data))
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
