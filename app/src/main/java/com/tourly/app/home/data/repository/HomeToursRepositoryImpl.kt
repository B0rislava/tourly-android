package com.tourly.app.home.data.repository

import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.api.TourApiService
import com.tourly.app.core.network.model.CreateTourResponseDto
import com.tourly.app.home.data.mapper.TourMapper
import com.tourly.app.home.domain.repository.HomeToursRepository
import com.tourly.app.core.network.mapper.TagMapper
import com.tourly.app.core.network.model.TagDto
import com.tourly.app.home.domain.model.Tag
import com.tourly.app.home.domain.model.TourFilters
import com.tourly.app.home.domain.model.Tour
import javax.inject.Inject

class HomeToursRepositoryImpl @Inject constructor(
    private val apiService: TourApiService
) : HomeToursRepository {

    override suspend fun getAllTags(): Result<List<Tag>> {
        return when (val result = NetworkResponseMapper.map<List<TagDto>>(apiService.getAllTags())) {
            is Result.Success -> Result.Success(result.data.map { TagMapper.toDomain(it) })
            is Result.Error -> result
        }
    }

    override suspend fun getAllTours(filters: TourFilters): Result<List<Tour>> {
        return when (val result = NetworkResponseMapper.map<List<CreateTourResponseDto>>(apiService.getAllTours(filters))) {
            is Result.Success -> Result.Success(TourMapper.toDomainList(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getTourDetails(id: Long): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto>(apiService.getTour(id))) {
            is Result.Success -> Result.Success(TourMapper.toDomain(result.data))
            is Result.Error -> result
        }
    }
}
