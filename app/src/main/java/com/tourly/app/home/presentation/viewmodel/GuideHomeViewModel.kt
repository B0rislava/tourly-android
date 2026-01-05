package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.home.presentation.state.GuideHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuideHomeViewModel @Inject constructor(
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuideHomeUiState())
    val uiState: StateFlow<GuideHomeUiState> = _uiState.asStateFlow()

    init {
        loadTours()
    }

    fun loadTours() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = tourRepository.getMyTours()
            result.onSuccess { tours ->
                _uiState.update { it.copy(tours = tours, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message, isLoading = false) }
            }
        }
    }
}
