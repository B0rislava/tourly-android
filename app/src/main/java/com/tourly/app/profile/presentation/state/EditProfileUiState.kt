package com.tourly.app.profile.presentation.state

import android.net.Uri

data class EditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val profilePictureUrl: String? = null,
    val profilePictureUri: Uri? = null
)