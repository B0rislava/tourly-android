package com.tourly.app.core.presentation.state

import com.tourly.app.core.network.model.UserDto
import com.tourly.app.profile.presentation.state.EditProfileUiState

sealed interface UserUiState {
    data object Idle : UserUiState
    data object Loading : UserUiState
    data class Success(
        val user: UserDto,
        val isEditing: Boolean = false,
        val editState: EditProfileUiState = EditProfileUiState()
    ) : UserUiState
    data class Error(val message: String) : UserUiState
}
