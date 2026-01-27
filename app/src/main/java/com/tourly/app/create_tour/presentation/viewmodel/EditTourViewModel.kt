package com.tourly.app.create_tour.presentation.viewmodel

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.usecase.UpdateTourUseCase
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.util.InputFormatter
import com.tourly.app.home.domain.usecase.GetAllTagsUseCase
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class EditTourViewModel @Inject constructor(
    private val updateTourUseCase: UpdateTourUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val inputFormatter: InputFormatter,
    private val placesClient: PlacesClient,
    @param:ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var tourId: Long = -1
    private val geocoder = Geocoder(context, Locale.getDefault())

    private val _uiState = MutableStateFlow(CreateTourUiState())
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

    private val _addressPredictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val addressPredictions: StateFlow<List<AutocompletePrediction>> = _addressPredictions.asStateFlow()

    private val _events = Channel<CreateTourEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadTags()
    }

    fun setTourId(id: Long) {
        if (tourId == id) return
        tourId = id
        loadTourDetails()
    }

    private fun loadTourDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getTourDetailsUseCase(tourId)) {
                is Result.Success -> {
                    val tour = result.data
                    _uiState.update { state ->
                        state.copy(
                            title = tour.title,
                            description = tour.description,
                            location = tour.location,
                            duration = tour.duration.replace(":", ""),
                            maxGroupSize = tour.maxGroupSize.toString(),
                            pricePerPerson = tour.pricePerPerson.toString(),
                            whatsIncluded = tour.whatsIncluded ?: "",
                            scheduledDate = tour.scheduledDate.let {
                                LocalDate.parse(it).atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
                            },
                            latitude = tour.latitude,
                            longitude = tour.longitude,
                            meetingPointAddress = tour.meetingPoint ?: "",
                            imageUri = tour.imageUrl?.toUri(),
                            selectedTagIds = tour.tags.map { it.id }.toSet(),
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(CreateTourEvent.Error("Failed to load tour details: ${result.message}"))
                }
            }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            when (val result = getAllTagsUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(availableTags = result.data) }
                }
                is Result.Error -> {
                    _events.send(CreateTourEvent.Error("Failed to load tags"))
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun onDurationChanged(duration: String) {
        val formatted = inputFormatter.formatDuration(duration)
        _uiState.update { it.copy(duration = formatted, durationError = null) }
    }

    fun onMaxGroupSizeChanged(sizeStr: String) {
        val formatted = inputFormatter.formatGroupSize(sizeStr)
        _uiState.update { it.copy(maxGroupSize = formatted, maxGroupSizeError = null) }
    }

    fun onPriceChanged(priceStr: String) {
        val formatted = inputFormatter.formatPrice(priceStr)
        _uiState.update { it.copy(pricePerPerson = formatted, priceError = null) }
    }

    fun onWhatsIncludedChanged(included: String) {
        _uiState.update { it.copy(whatsIncluded = included) }
    }

    fun onScheduledDateChanged(date: Long?) {
        _uiState.update { it.copy(scheduledDate = date, dateError = null) }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun onTagToggled(tagId: Long) {
        _uiState.update { state ->
            val currentIds = state.selectedTagIds
            val newIds = if (tagId in currentIds) {
                currentIds - tagId
            } else {
                currentIds + tagId
            }
            state.copy(selectedTagIds = newIds)
        }
    }

    fun onMeetingPointSelected(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude) }
        reverseGeocode(latitude, longitude)
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                val address = addresses.firstOrNull()
                if (address != null) {
                    val fullAddress = address.getAddressLine(0) ?: ""
                    val city = address.locality ?: ""
                    val country = address.countryName ?: ""
                    val generalLocation = if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }
                    
                    _uiState.update { it.copy(
                        meetingPointAddress = fullAddress,
                        location = generalLocation.ifEmpty { it.location }
                    ) }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()
            if (address != null) {
                val fullAddress = address.getAddressLine(0) ?: ""
                val city = address.locality ?: ""
                val country = address.countryName ?: ""
                val generalLocation = if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }

                _uiState.update { it.copy(
                    meetingPointAddress = fullAddress,
                    location = generalLocation.ifEmpty { it.location }
                ) }
            }
        }
    }

    fun onMeetingPointAddressChanged(address: String) {
        _uiState.update { it.copy(meetingPointAddress = address, meetingPointAddressError = null) }
        fetchAddressPredictions(address)
    }

    private fun fetchAddressPredictions(query: String) {
        if (query.length < 3) {
            _addressPredictions.value = emptyList()
            return
        }

        val locationBias = _uiState.value.let { state ->
            if (state.latitude != null && state.longitude != null) {
                val lat = state.latitude
                val lng = state.longitude
                RectangularBounds.newInstance(
                    LatLng(lat - 0.1, lng - 0.1),
                    LatLng(lat + 0.1, lng + 0.1)
                )
            } else {
                null
            }
        }

        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
        
        if (locationBias != null) {
            requestBuilder.setLocationBias(locationBias)
        }

        placesClient.findAutocompletePredictions(requestBuilder.build())
            .addOnSuccessListener { response ->
                _addressPredictions.value = response.autocompletePredictions
            }
            .addOnFailureListener {
                _addressPredictions.value = emptyList()
            }
    }

    fun onLocationSelected(prediction: AutocompletePrediction) {
        _uiState.update { 
            it.copy(
                meetingPointAddress = prediction.getFullText(null).toString(),
                locationError = null
            )
        }
        
        val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.LOCATION, Place.Field.FORMATTED_ADDRESS, Place.Field.ADDRESS_COMPONENTS)
        val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val location = place.location
                
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
                
                val generalLocation = if (city.isNotEmpty() && country.isNotEmpty()) {
                    "$city, $country"
                } else {
                    place.addressComponents?.asList()?.find { it.types.contains("administrative_area_level_1") }?.name
                        ?: place.displayName
                        ?: ""
                }

                _uiState.update { 
                    it.copy(
                        meetingPointAddress = place.formattedAddress ?: prediction.getFullText(null).toString(),
                        location = generalLocation,
                        latitude = location?.latitude,
                        longitude = location?.longitude,
                        locationError = null
                    )
                }
                _addressPredictions.value = emptyList()
            }
    }

    fun onUpdateTour() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val params = mapToParams(_uiState.value)

            when (val result = updateTourUseCase(tourId, params)) {
                is Result.Success -> {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    _events.send(CreateTourEvent.Success)
                }
                is Result.Error -> {
                    handleError(result)
                    _events.send(CreateTourEvent.Error(result.message))
                }
            }
        }
    }

    private fun mapToParams(state: CreateTourUiState): CreateTourParams {
        return CreateTourParams(
            title = state.title,
            description = state.description,
            location = state.location,
            duration = formatDurationToHHmm(state.duration),
            maxGroupSize = state.maxGroupSize.toIntOrNull() ?: 0,
            pricePerPerson = state.pricePerPerson.toDoubleOrNull() ?: 0.0,
            whatsIncluded = state.whatsIncluded.ifBlank { null },
            scheduledDate = state.scheduledDate?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
            },
            latitude = state.latitude,
            longitude = state.longitude,
            meetingPoint = state.meetingPointAddress.ifBlank { null },
            imageUri = state.imageUri,
            tagIds = state.selectedTagIds.toList()
        )
    }

    private fun formatDurationToHHmm(digits: String): String {
        val padded = digits.padStart(4, '0')
        return "${padded.take(2)}:${padded.substring(2)}"
    }

    private fun handleError(error: Result.Error) {
        _uiState.update { state ->
            val message = error.message
            when {
                message.contains("Title") ->
                    state.copy(isLoading = false, titleError = message)
                message.contains("Description") ->
                    state.copy(isLoading = false, descriptionError = message)
                message.contains("Location") ->
                    state.copy(isLoading = false, locationError = message)
                message.contains("Duration") ->
                    state.copy(isLoading = false, durationError = message)
                message.contains("group size") ->
                    state.copy(isLoading = false, maxGroupSizeError = message)
                message.contains("Price") ->
                    state.copy(isLoading = false, priceError = message)
                message.contains("Date") ->
                    state.copy(isLoading = false, dateError = message)
                else ->
                    state.copy(isLoading = false, createTourError = message)
            }
        }
    }
}
