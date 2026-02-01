package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.model.PlaceDetails
import com.tourly.app.core.domain.repository.LocationRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class GetPlaceDetailsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(placeId: String): Result<PlaceDetails> {
        return repository.getPlaceDetails(placeId)
    }
}
