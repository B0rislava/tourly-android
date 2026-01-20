package com.tourly.app.home.domain.usecase

import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class GetTourDetailsUseCase @Inject constructor(
    private val repository: HomeToursRepository
) {
    suspend operator fun invoke(id: Long): Result<Tour> {
        return repository.getTourDetails(id)
    }
}
