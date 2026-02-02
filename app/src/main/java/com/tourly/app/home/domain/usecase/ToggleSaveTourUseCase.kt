package com.tourly.app.home.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class ToggleSaveTourUseCase @Inject constructor(
    private val repository: HomeToursRepository
) {
    suspend operator fun invoke(tourId: Long): Result<Boolean> {
        return repository.toggleSaveTour(tourId)
    }
}
