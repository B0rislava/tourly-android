package com.tourly.app.core.data.repository

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tourly.app.core.domain.model.LocationPrediction
import com.tourly.app.core.domain.model.PlaceDetails
import com.tourly.app.core.domain.model.ReverseGeocodedAddress
import com.tourly.app.core.domain.repository.LocationRepository
import com.tourly.app.core.network.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val placesClient: PlacesClient,
    @param:ApplicationContext private val context: Context
) : LocationRepository {

    private val geocoder = Geocoder(context, Locale.getDefault())

    override suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): Result<ReverseGeocodedAddress> {
        return suspendCancellableCoroutine { continuation ->
            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val address = addresses.firstOrNull()
                        if (address != null) {
                            val city = address.locality ?: ""
                            val country = address.countryName ?: ""
                            val displayLocation = if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }
                            
                            continuation.resume(Result.Success(
                                ReverseGeocodedAddress(
                                    fullAddress = address.getAddressLine(0) ?: "",
                                    city = city,
                                    country = country,
                                    displayLocation = displayLocation
                                )
                            ))
                        } else {
                            continuation.resume(Result.Error("GEOCODER_ERROR", "No address found"))
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null) {
                        val city = address.locality ?: ""
                        val country = address.countryName ?: ""
                        val displayLocation = if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }

                        continuation.resume(Result.Success(
                            ReverseGeocodedAddress(
                                fullAddress = address.getAddressLine(0) ?: "",
                                city = city,
                                country = country,
                                displayLocation = displayLocation
                            )
                        ))
                    } else {
                        continuation.resume(Result.Error("GEOCODER_ERROR", "No address found"))
                    }
                }
            } catch (e: Exception) {
                continuation.resume(Result.Error("GEOCODER_ERROR", e.message ?: "Unknown error"))
            }
        }
    }

    override suspend fun searchLocations(
        query: String,
        latitude: Double?,
        longitude: Double?
    ): Result<List<LocationPrediction>> {
        return try {
            val requestBuilder = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
            
            if (latitude != null && longitude != null) {
                val bias = RectangularBounds.newInstance(
                    LatLng(latitude - 0.1, longitude - 0.1),
                    LatLng(latitude + 0.1, longitude + 0.1)
                )
                requestBuilder.setLocationBias(bias)
            }

            val response = placesClient.findAutocompletePredictions(requestBuilder.build()).await()
            
            val predictions = response.autocompletePredictions.map { prediction ->
                LocationPrediction(
                    id = prediction.placeId,
                    description = prediction.getFullText(null).toString(),
                    primaryText = prediction.getPrimaryText(null).toString(),
                    secondaryText = prediction.getSecondaryText(null).toString()
                )
            }
            
            Result.Success(predictions)
        } catch (e: Exception) {
            Result.Error(code = "PLACES_ERROR", message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getPlaceDetails(placeId: String): Result<PlaceDetails> {
        return try {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.LOCATION,
                Place.Field.FORMATTED_ADDRESS,
                Place.Field.ADDRESS_COMPONENTS
            )
            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            val response = placesClient.fetchPlace(request).await()
            val place = response.place
            
            var city = ""
            var country = ""
            place.addressComponents?.asList()?.forEach { component ->
                if (component.types.contains("locality")) {
                    city = component.name
                }
                if (component.types.contains("country")) {
                    country = component.name
                }
            }
            
            Result.Success(
                PlaceDetails(
                    id = place.id ?: placeId,
                    name = place.displayName ?: "",
                    address = place.formattedAddress ?: "",
                    latitude = place.location?.latitude ?: 0.0,
                    longitude = place.location?.longitude ?: 0.0,
                    city = city,
                    country = country
                )
            )
        } catch (e: Exception) {
            Result.Error(code = "PLACES_ERROR", message = e.message ?: "Unknown error")
        }
    }
}
