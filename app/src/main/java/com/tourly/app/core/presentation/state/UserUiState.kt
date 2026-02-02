package com.tourly.app.core.presentation.state

import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.profile.presentation.state.EditProfileUiState

sealed interface UserUiState {
    data object Idle : UserUiState
    data object Loading : UserUiState
    data class Success(
        val user: User,
        val isOwnProfile: Boolean = true,
        val isEditing: Boolean = false,
        val editState: EditProfileUiState = EditProfileUiState(),
        val bookings: List<Booking> = emptyList(),
        val tours: List<Tour> = emptyList(),
        val savedTours: List<Tour> = emptyList()
    ) : UserUiState
    data class Error(val message: String) : UserUiState
}
