package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import com.tourly.app.home.domain.usecase.BookTourUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TourDetailsUiState {
    data object Loading : TourDetailsUiState
    data class Error(val message: String) : TourDetailsUiState
    data class Success(
        val tour: Tour,
        val isBooking: Boolean = false,
        val bookingError: String? = null,
        val isBookingSuccess: Boolean = false
    ) : TourDetailsUiState
}

@HiltViewModel
class TourDetailsViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase,
    private val bookTourUseCase: BookTourUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TourDetailsUiState>(TourDetailsUiState.Loading)
    val uiState: StateFlow<TourDetailsUiState> = _uiState.asStateFlow()

    fun loadTour(id: Long) {
        viewModelScope.launch {
            _uiState.value = TourDetailsUiState.Loading
            when (val result = getTourDetailsUseCase(id)) {
                is Result.Success -> {
                    _uiState.value = TourDetailsUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = TourDetailsUiState.Error(result.message)
                }
            }
        }
    }

    fun bookTour(tourId: Long, numberOfParticipants: Int) {
        val currentState = _uiState.value
        if (currentState !is TourDetailsUiState.Success) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isBooking = true, bookingError = null)

            when (val result = bookTourUseCase(tourId, numberOfParticipants)) {
                is Result.Success -> {
                    // Reload tour to update available spots
                    val reloadResult = getTourDetailsUseCase(tourId)
                    if (reloadResult is Result.Success) {
                        _uiState.value = currentState.copy(
                            tour = reloadResult.data,
                            isBooking = false,
                            isBookingSuccess = true
                        )
                    } else {
                        // Even if reload fails, booking was success
                        _uiState.value = currentState.copy(isBooking = false, isBookingSuccess = true)
                    }
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(
                        isBooking = false,
                        bookingError = result.message
                    )
                }
            }
        }
    }

    fun resetBookingState() {
        val currentState = _uiState.value
        if (currentState is TourDetailsUiState.Success) {
            _uiState.value = currentState.copy(
                isBooking = false,
                bookingError = null,
                isBookingSuccess = false
            )
        }
    }
}
