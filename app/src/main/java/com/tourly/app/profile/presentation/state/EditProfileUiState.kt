package com.tourly.app.profile.presentation.state

import android.net.Uri
import com.tourly.app.login.domain.UserRole

data class EditProfileUiState(
    val userRole: UserRole? = null,
    val fullName: String = "",
    val email: String = "",
    val bio: String = "",
    val certifications: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: Int? = null,
    val emailError: Int? = null,
    val bioError: Int? = null,
    val certificationsError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val profilePictureUrl: String? = null,
    val profilePictureUri: Uri? = null
)