package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TourDetailsUiState {
    data object Loading : TourDetailsUiState
    data class Success(val tour: Tour) : TourDetailsUiState
    data class Error(val message: String) : TourDetailsUiState
}

@HiltViewModel
class TourDetailsViewModel @Inject constructor(
    private val getTourDetailsUseCase: GetTourDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TourDetailsUiState>(TourDetailsUiState.Loading)
    val uiState: StateFlow<TourDetailsUiState> = _uiState.asStateFlow()

    fun loadTour(id: Long) {
        viewModelScope.launch {
            _uiState.value = TourDetailsUiState.Loading
            getTourDetailsUseCase(id)
                .onSuccess { tour ->
                    _uiState.value = TourDetailsUiState.Success(tour)
                }
                .onFailure { error ->
                    _uiState.value = TourDetailsUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}
