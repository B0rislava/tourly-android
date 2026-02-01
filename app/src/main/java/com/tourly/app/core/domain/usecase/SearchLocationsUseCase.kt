package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.model.LocationPrediction
import com.tourly.app.core.domain.repository.LocationRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class SearchLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(query: String, latitude: Double? = null, longitude: Double? = null): Result<List<LocationPrediction>> {
        return repository.searchLocations(query, latitude, longitude)
    }
}
