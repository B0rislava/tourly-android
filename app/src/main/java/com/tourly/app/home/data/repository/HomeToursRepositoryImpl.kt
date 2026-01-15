package com.tourly.app.home.data.repository

import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result as NetworkResult
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class HomeToursRepositoryImpl @Inject constructor(
    private val apiService: TourApiService
) : HomeToursRepository {

    override suspend fun getAllTours(): Result<List<Tour>> {
        return try {
            val result = NetworkResponseMapper.map<List<CreateTourResponseDto>>(apiService.getAllTours())
            
            when (result) {
                is NetworkResult.Success -> Result.success(TourMapper.toDomainList(result.data))
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
