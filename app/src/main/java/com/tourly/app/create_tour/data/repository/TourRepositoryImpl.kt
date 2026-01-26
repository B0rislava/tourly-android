package com.tourly.app.create_tour.data.repository

import android.content.Context
import android.net.Uri
import com.tourly.app.core.network.NetworkResponseMapper
import com.tourly.app.core.network.Result
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

    override suspend fun createTour(
        context: Context,
        request: CreateTourRequestDto,
        imageUri: Uri?
    ): Result<Tour> {
        return when (val result = NetworkResponseMapper.map<CreateTourResponseDto>(
            apiService.createTour(context, request, imageUri)
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
}
