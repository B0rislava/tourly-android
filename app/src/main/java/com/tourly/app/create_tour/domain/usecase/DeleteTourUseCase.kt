package com.tourly.app.create_tour.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.repository.TourRepository
import javax.inject.Inject

class DeleteTourUseCase @Inject constructor(
    private val repository: TourRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteTour(id)
    }
}
