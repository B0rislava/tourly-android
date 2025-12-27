package com.tourly.app

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val isUserLoggedIn: Boolean) : MainActivityUiState
}
