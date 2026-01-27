package com.tourly.app.create_tour.domain.repository

import android.content.Context
import android.net.Uri
import com.tourly.app.core.network.Result
import com.tourly.app.core.network.model.CreateTourRequestDto
import com.tourly.app.home.domain.model.Tour

interface TourRepository {
    suspend fun createTour(
        context: Context,
        request: CreateTourRequestDto,
        imageUri: Uri?
    ): Result<Tour>
    suspend fun getMyTours(): Result<List<Tour>>
    suspend fun updateTour(
        context: Context,
        id: Long,
        request: CreateTourRequestDto,
        imageUri: Uri?
    ): Result<Tour>
    suspend fun deleteTour(id: Long): Result<Unit>
}
