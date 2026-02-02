package com.tourly.app.home.domain.usecase

import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.home.domain.repository.HomeToursRepository
import javax.inject.Inject

class GetSavedToursUseCase @Inject constructor(
    private val repository: HomeToursRepository
) {
    suspend operator fun invoke(): Result<List<Tour>> {
        return repository.getSavedTours()
    }
}
