package com.tourly.app.home.domain.repository

import com.tourly.app.home.domain.model.Tour

interface HomeToursRepository {
    suspend fun getAllTours(): Result<List<Tour>>
}
