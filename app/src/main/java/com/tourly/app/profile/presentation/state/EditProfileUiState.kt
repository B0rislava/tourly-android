package com.tourly.app.profile.presentation.state

import android.net.Uri

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val bio: String = "",
    val certifications: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val bioError: String? = null,
    val certificationsError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val profilePictureUrl: String? = null,
    val profilePictureUri: Uri? = null
)