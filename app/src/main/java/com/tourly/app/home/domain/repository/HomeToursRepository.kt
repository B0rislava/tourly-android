package com.tourly.app.home.domain.repository

import com.tourly.app.home.domain.model.Tag
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.model.TourFilters

interface HomeToursRepository {
    suspend fun getAllTags(): Result<List<Tag>>
    suspend fun getAllTours(filters: TourFilters): Result<List<Tour>>
    suspend fun getTourDetails(id: Long): Result<Tour>
}
