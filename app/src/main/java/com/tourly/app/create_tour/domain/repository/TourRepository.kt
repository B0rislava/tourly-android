package com.tourly.app.create_tour.domain.repository

import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.home.domain.model.Tour

interface TourRepository {
    suspend fun createTour(request: CreateTourRequestDto): Result<Tour>
    suspend fun getMyTours(): Result<List<Tour>>
}
