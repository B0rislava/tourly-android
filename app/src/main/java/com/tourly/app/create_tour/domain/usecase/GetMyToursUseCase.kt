package com.tourly.app.create_tour.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.core.domain.model.Tour
import javax.inject.Inject

class GetMyToursUseCase @Inject constructor(
    private val tourRepository: TourRepository
) {
    suspend operator fun invoke(): Result<List<Tour>> {
        return tourRepository.getMyTours()
    }
}
