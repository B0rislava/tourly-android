package com.tourly.app.home.presentation.state

import com.tourly.app.core.domain.model.Tour

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val tours: List<Tour>) : HomeUiState
    data class Error(val message: String, val code: String? = null) : HomeUiState
}
