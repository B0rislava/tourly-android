package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.core.domain.model.Review
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import com.tourly.app.home.domain.usecase.BookTourUseCase
import com.tourly.app.home.domain.usecase.ToggleSaveTourUseCase
import com.tourly.app.reviews.domain.usecase.GetTourReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TourDetailsEvent {
    data class ShowSnackbar(val message: String) : TourDetailsEvent
    data object BookingSuccess : TourDetailsEvent
}

sealed interface TourDetailsUiState {
    data object Loading : TourDetailsUiState
    data class Error(val message: String, val code: String? = null) : TourDetailsUiState
    data class Success(
        val tour: Tour,
        val reviews: List<Review> = emptyList(),
        val isBooking: Boolean = false,
        val isSavingTour: Boolean = false
    ) : TourDetailsUiState
}

@HiltViewModel
class TourDetailsViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val bookTourUseCase: BookTourUseCase,
    private val toggleSaveTourUseCase: ToggleSaveTourUseCase,
    private val getTourReviewsUseCase: GetTourReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TourDetailsUiState>(TourDetailsUiState.Loading)
    val uiState: StateFlow<TourDetailsUiState> = _uiState.asStateFlow()

    private var currentTourId: Long? = null

    private val _events = Channel<TourDetailsEvent>()
    val events = _events.receiveAsFlow()

    fun toggleSaveTour(tourId: Long) {
        val currentState = _uiState.value
        if (currentState !is TourDetailsUiState.Success) return

        val tour = currentState.tour
        val newIsSaved = !tour.isSaved

        _uiState.value = currentState.copy(
            tour = tour.copy(isSaved = newIsSaved),
            isSavingTour = true
        )

        viewModelScope.launch {
            when (val result = toggleSaveTourUseCase(tourId)) {
                is Result.Success -> {
                    val current = _uiState.value
                    if (current is TourDetailsUiState.Success) {
                        _uiState.value = current.copy(
                            tour = tour.copy(isSaved = result.data),
                            isSavingTour = false
                        )
                    }
                }
                is Result.Error -> {
                    val current = _uiState.value
                    if (current is TourDetailsUiState.Success) {
                        // Revert optimistic update
                        _uiState.value = current.copy(
                            tour = tour,
                            isSavingTour = false
                        )
                    }
                    _events.send(TourDetailsEvent.ShowSnackbar(result.message))
                }
            }
        }
    }

    fun loadTour(id: Long) {
        currentTourId = id
        viewModelScope.launch {
            _uiState.value = TourDetailsUiState.Loading
            when (val result = getTourDetailsUseCase(id)) {
                is Result.Success -> {
                    val tour = result.data
                    // Fetch reviews
                    val reviews = when (val reviewResult = getTourReviewsUseCase(id)) {
                        is Result.Success -> reviewResult.data
                        else -> emptyList() // Ignore error for now, show tour
                    }
                    _uiState.value = TourDetailsUiState.Success(tour = tour, reviews = reviews)
                }
                is Result.Error -> {
                    _uiState.value = TourDetailsUiState.Error(result.message, result.code)
                }
            }
        }
    }

    fun bookTour(tourId: Long, numberOfParticipants: Int) {
        val currentState = _uiState.value
        if (currentState !is TourDetailsUiState.Success) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isBooking = true)

            when (val result = bookTourUseCase(tourId, numberOfParticipants)) {
                is Result.Success -> {
                    // Reload tour to update available spots
                    val reloadResult = getTourDetailsUseCase(tourId)
                    val current = _uiState.value
                    if (current is TourDetailsUiState.Success) {
                        if (reloadResult is Result.Success) {
                            _uiState.value = current.copy(
                                tour = reloadResult.data,
                                isBooking = false
                            )
                        } else {
                            // Even if reload fails, booking was success
                            _uiState.value = current.copy(isBooking = false)
                        }
                    }
                    _events.send(TourDetailsEvent.BookingSuccess)
                }
                is Result.Error -> {
                    val current = _uiState.value
                    if (current is TourDetailsUiState.Success) {
                        _uiState.value = current.copy(isBooking = false)
                    }
                    _events.send(TourDetailsEvent.ShowSnackbar(result.message))
                }
            }
        }
    }

    fun retry() {
        currentTourId?.let { loadTour(it) }
    }
}

