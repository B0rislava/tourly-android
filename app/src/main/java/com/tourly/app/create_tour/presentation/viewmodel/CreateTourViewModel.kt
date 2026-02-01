package com.tourly.app.create_tour.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.model.LocationPrediction
import com.tourly.app.core.domain.usecase.GetAddressFromCoordinatesUseCase
import com.tourly.app.core.domain.usecase.GetPlaceDetailsUseCase
import com.tourly.app.core.domain.usecase.SearchLocationsUseCase
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.usecase.CreateTourUseCase
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.util.InputFormatter
import com.tourly.app.home.domain.usecase.GetAllTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CreateTourViewModel @Inject constructor(
    private val createTourUseCase: CreateTourUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val inputFormatter: InputFormatter,
    private val searchLocationsUseCase: SearchLocationsUseCase,
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
) : ViewModel() {

    init {
        loadTags()
    }

    private val _uiState = MutableStateFlow(CreateTourUiState())
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

    private val _addressPredictions = MutableStateFlow<List<LocationPrediction>>(emptyList())
    val addressPredictions: StateFlow<List<LocationPrediction>> = _addressPredictions.asStateFlow()

    private val _events = Channel<CreateTourEvent>()
    val events = _events.receiveAsFlow()

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


    fun onLocationSelected(prediction: LocationPrediction) {
        // Immediate update with prediction text
        _uiState.update { 
            it.copy(
                meetingPointAddress = prediction.description,
                locationError = null
            )
        }
        
        viewModelScope.launch {
            when (val result = getPlaceDetailsUseCase(prediction.id)) {
                is Result.Success -> {
                    val place = result.data
                    
                    // Fallback for Title/Location display: City, Country
                    val generalLocation = if (place.city.isNotEmpty() && place.country.isNotEmpty()) {
                        "${place.city}, ${place.country}"
                    } else {
                        place.city.ifEmpty { place.name }
                    }

                    _uiState.update { 
                        it.copy(
                            meetingPointAddress = place.address.ifEmpty { prediction.description },
                            location = generalLocation,
                            latitude = place.latitude,
                            longitude = place.longitude,
                            locationError = null
                        )
                    }
                    _addressPredictions.value = emptyList()
                }
                is Result.Error -> {
                    // TODO: Handle error
                }
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

        viewModelScope.launch {
            val state = _uiState.value
            val result = searchLocationsUseCase(query, state.latitude, state.longitude)

            when (result) {
                is Result.Success -> {
                    _addressPredictions.value = result.data
                }
                is Result.Error -> {
                    _addressPredictions.value = emptyList()
                }
            }
        }
    }


    fun onMeetingPointSelected(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude) }
        reverseGeocode(latitude, longitude)
    }

    private fun reverseGeocode(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            when (val result = getAddressFromCoordinatesUseCase(latitude, longitude)) {
                is Result.Success -> {
                    val addressContent = result.data
                    _uiState.update { it.copy(
                        meetingPointAddress = addressContent.fullAddress,
                        location = addressContent.displayLocation.ifEmpty { it.location }
                    ) }
                }
                is Result.Error -> {
                    // TODO: Handle error
                }
            }
        }
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

    fun onStartTimeChanged(time: java.time.LocalTime?) {
        _uiState.update { it.copy(startTime = time, timeError = null) }
    }

    fun onImageSelected(uri: android.net.Uri?) {
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

    private fun loadTags() {
        viewModelScope.launch {
            when (val result = getAllTagsUseCase()) {
                is Result.Success -> {
                    val tags = result.data
                    _uiState.update { it.copy(availableTags = tags) }
                }
                is Result.Error -> {
                    _events.send(CreateTourEvent.Error("Failed to load filters"))
                }
            }
        }
    }

    fun onCreateTour() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val params = mapToParams(_uiState.value)

            when (val result = createTourUseCase(params)) {
                is Result.Success -> {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    resetState()
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
            startTime = state.startTime,
            latitude = state.latitude,
            longitude = state.longitude,
            meetingPoint = state.meetingPointAddress.ifBlank { null },
            imageUri = state.imageUri,
            tagIds = state.selectedTagIds.toList()
        )
    }

    private fun handleError(error: Result.Error) {
        _uiState.update { state ->
            val message = error.message
            when (error.code) {
                "INVALID_TITLE" ->
                    state.copy(isLoading = false, titleError = message)
                "INVALID_DESCRIPTION" ->
                    state.copy(isLoading = false, descriptionError = message)
                "INVALID_LOCATION" ->
                    state.copy(isLoading = false, locationError = message)
                "INVALID_DURATION", "INVALID_DURATION_RANGE" ->
                    state.copy(isLoading = false, durationError = message)
                "INVALID_GROUP_SIZE" ->
                    state.copy(isLoading = false, maxGroupSizeError = message)
                "INVALID_PRICE" ->
                    state.copy(isLoading = false, priceError = message)
                "DATE_REQUIRED", "DATE_IN_PAST" ->
                    state.copy(isLoading = false, dateError = message)
                else ->
                    state.copy(isLoading = false, createTourError = message)
            }
        }
    }

    private fun formatDurationToHHmm(digits: String): String {
        val padded = digits.padStart(4, '0')
        return "${padded.take(2)}:${padded.substring(2)}"
    }

    fun resetState() {
        _uiState.update { state -> 
            CreateTourUiState(availableTags = state.availableTags) 
        }
    }
}
