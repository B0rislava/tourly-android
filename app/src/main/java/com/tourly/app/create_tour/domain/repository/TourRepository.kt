package com.tourly.app.create_tour.domain.repository

import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.core.network.model.CreateTourResponseDto

interface TourRepository {
    suspend fun createTour(request: CreateTourRequestDto): Result<CreateTourResponseDto>
    suspend fun getMyTours(): Result<List<CreateTourResponseDto>>
}
