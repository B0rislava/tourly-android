package com.tourly.app.core.domain.repository

import com.tourly.app.core.domain.model.LocationPrediction
import com.tourly.app.core.domain.model.PlaceDetails
import com.tourly.app.core.network.Result

interface LocationRepository {
    suspend fun searchLocations(query: String, latitude: Double? = null, longitude: Double? = null): Result<List<LocationPrediction>>
    suspend fun getPlaceDetails(placeId: String): Result<PlaceDetails>
}
