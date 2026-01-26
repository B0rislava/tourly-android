package com.tourly.app.create_tour.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.create_tour.domain.exception.CreateTourException
import com.tourly.app.create_tour.domain.model.CreateTourParams
import com.tourly.app.create_tour.domain.usecase.CreateTourUseCase
import com.tourly.app.home.domain.usecase.GetAllTagsUseCase
import com.tourly.app.create_tour.presentation.state.CreateTourUiState
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.util.InputFormatter
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
    private val inputFormatter: InputFormatter
) : ViewModel() {

    init {
        loadTags()
    }

    private val _uiState = MutableStateFlow(CreateTourUiState())
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

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

    fun onLocationChanged(location: String) {
        _uiState.update { it.copy(location = location, locationError = null) }
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
            getAllTagsUseCase().onSuccess { tags ->
                println("CreateTourVM: Loaded ${tags.size} tags: ${tags.map { it.displayName }}")
                _uiState.update { it.copy(availableTags = tags) }
            }.onFailure { e ->
                println("CreateTourVM: Failed to load tags: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun onCreateTour() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val params = mapToParams(_uiState.value)

            createTourUseCase(params)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false) }
                    resetState()
                    _events.send(CreateTourEvent.Success)
                }
                .onFailure { error ->
                    handleError(error)
                    _events.send(CreateTourEvent.Error(error.message ?: "Failed to create tour"))
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
            imageUri = state.imageUri,
            tagIds = state.selectedTagIds.toList()
        )
    }

    private fun handleError(error: Throwable) {
        _uiState.update { state ->
            when (error) {
                is CreateTourException.InvalidTitle ->
                    state.copy(isLoading = false, titleError = error.message)
                is CreateTourException.InvalidDescription ->
                    state.copy(isLoading = false, descriptionError = error.message)
                is CreateTourException.InvalidLocation ->
                    state.copy(isLoading = false, locationError = error.message)
                is CreateTourException.InvalidDuration,
                is CreateTourException.InvalidDurationRange ->
                    state.copy(isLoading = false, durationError = error.message)
                is CreateTourException.InvalidGroupSize ->
                    state.copy(isLoading = false, maxGroupSizeError = error.message)
                is CreateTourException.InvalidPrice ->
                    state.copy(isLoading = false, priceError = error.message)
                is CreateTourException.DateRequired,
                is CreateTourException.DateInPast ->
                    state.copy(isLoading = false, dateError = error.message)
                else ->
                    state.copy(isLoading = false, createTourError = error.message)
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



