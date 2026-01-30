package com.tourly.app.create_tour.domain.repository

import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.data.dto.CreateTourRequestDto
import com.tourly.app.core.domain.model.Tour

interface TourRepository {
    suspend fun createTour(
        request: CreateTourRequestDto,
        imageUri: String?
    ): Result<Tour>
    
    suspend fun getMyTours(): Result<List<Tour>>
    
    suspend fun updateTour(
        id: Long,
        request: CreateTourRequestDto,
        imageUri: String?
    ): Result<Tour>
    
    suspend fun deleteTour(id: Long): Result<Unit>
}
