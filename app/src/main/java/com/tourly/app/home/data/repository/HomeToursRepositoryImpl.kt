package com.tourly.app.home.data.repository

import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result as NetworkResult
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.repository.HomeToursRepository
import com.tourly.app.core.network.mapper.TagMapper
import com.tourly.app.core.network.model.TagDto
import com.tourly.app.home.domain.model.Tag
import com.tourly.app.home.domain.model.TourFilters
import javax.inject.Inject

class HomeToursRepositoryImpl @Inject constructor(
    private val apiService: TourApiService
) : HomeToursRepository {

    override suspend fun getAllTags(): Result<List<Tag>> {
        return try {
            val result = NetworkResponseMapper.map<List<TagDto>>(apiService.getAllTags())

            when (result) {
                is NetworkResult.Success -> Result.success(result.data.map { TagMapper.toDomain(it) })
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTours(filters: TourFilters): Result<List<Tour>> {
        return try {
            val result = NetworkResponseMapper.map<List<CreateTourResponseDto>>(apiService.getAllTours(filters))
            
            when (result) {
                is NetworkResult.Success -> Result.success(TourMapper.toDomainList(result.data))
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTourDetails(id: Long): Result<Tour> {
        return try {
            val result = NetworkResponseMapper.map<CreateTourResponseDto>(apiService.getTour(id))

            when (result) {
                is NetworkResult.Success -> Result.success(TourMapper.toDomain(result.data))
                is NetworkResult.Error -> Result.failure(Exception(result.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
