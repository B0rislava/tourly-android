package com.tourly.app.home.presentation.state

import com.tourly.app.home.domain.model.Tour

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val tours: List<Tour>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
