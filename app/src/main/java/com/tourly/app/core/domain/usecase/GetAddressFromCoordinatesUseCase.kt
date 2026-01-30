package com.tourly.app.core.domain.usecase

import com.tourly.app.core.domain.model.ReverseGeocodedAddress
import com.tourly.app.core.domain.repository.LocationRepository
import com.tourly.app.core.network.Result
import javax.inject.Inject

class GetAddressFromCoordinatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<ReverseGeocodedAddress> {
        return repository.getAddressFromCoordinates(latitude, longitude)
    }
}
