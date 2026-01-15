package com.tourly.app.home.domain.usecase

import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class GetAllToursUseCase @Inject constructor(
    private val repository: HomeToursRepository
) {
    suspend operator fun invoke(): Result<List<Tour>> {
        return repository.getAllTours()
    }
}
