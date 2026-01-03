package com.tourly.app.core.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateProfilePictureUseCase
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.profile.presentation.state.EditProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val tokenManager: TokenManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    init {
        observeToken()
    }

    private fun observeToken() {
        viewModelScope.launch {
            tokenManager.getTokenFlow().collectLatest { token ->
                if (token != null) {
                    fetchUserProfile(token)
                } else {
                    _uiState.value = UserUiState.Idle
                }
            }
        }
    }

    private suspend fun fetchUserProfile(token: String) {
        _uiState.value = UserUiState.Loading
        
        getUserProfileUseCase(token)
            .onSuccess { user ->
                _uiState.value = UserUiState.Success(user)
            }
            .onFailure { error ->
                _uiState.value = UserUiState.Error(error.message ?: "Failed to load profile")
            }
    }

    fun startEditing() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            _uiState.value = currentState.copy(
                isEditing = true,
                editState = EditProfileUiState(
                    firstName = currentState.user.firstName,
                    lastName = currentState.user.lastName,
                    email = currentState.user.email,
                    profilePictureUrl = currentState.user.profilePictureUrl
                )
            )
        }
    }

    fun cancelEditing() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            _uiState.value = currentState.copy(
                isEditing = false,
                editState = EditProfileUiState()
            )
        }
    }

    fun onFirstNameChange(value: String) {
        updateEditState { it.copy(firstName = value, firstNameError = null) }
    }

    fun onLastNameChange(value: String) {
        updateEditState { it.copy(lastName = value, lastNameError = null) }
    }

    fun onEmailChange(value: String) {
        updateEditState { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        updateEditState { it.copy(password = value, passwordError = null) }
    }

    private fun updateEditState(update: (EditProfileUiState) -> EditProfileUiState) {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            _uiState.value = currentState.copy(
                editState = update(currentState.editState)
            )
        }
    }

    fun onProfilePictureSelected(uri: android.net.Uri) {
        updateEditState { it.copy(profilePictureUri = uri) }
    }

    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState !is UserUiState.Success) return
        
        if (!validateFields(currentState.editState)) return

        viewModelScope.launch {
            // Set loading state
            _uiState.value = currentState.copy(
                editState = currentState.editState.copy(isSaving = true, saveError = null)
            )

            val token = tokenManager.getToken()
            if (token == null) {
                _uiState.value = UserUiState.Idle
                return@launch
            }

            // Upload profile picture if changed
            var uploadError: String? = null
            if (currentState.editState.profilePictureUri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(currentState.editState.profilePictureUri)
                    val bytes = inputStream?.use { it.readBytes() }
                    
                    if (bytes != null) {
                        updateProfilePictureUseCase(token, bytes)
                            .onFailure { error ->
                                uploadError = "Failed to upload image: ${error.message}"
                            }
                    } else {
                        uploadError = "Failed to read image file"
                    }
                } catch (e: Exception) {
                    uploadError = "Error processing image: ${e.message}"
                }
            }

            if (uploadError != null) {
                _uiState.value = currentState.copy(
                    editState = currentState.editState.copy(
                        isSaving = false,
                        saveError = uploadError
                    )
                )
                return@launch
            }

            val request = UpdateProfileRequestDto(
                email = currentState.editState.email,
                firstName = currentState.editState.firstName,
                lastName = currentState.editState.lastName,
                password = currentState.editState.password.ifBlank { null }
            )

            updateUserProfileUseCase(token, request)
                .onSuccess { updatedUser ->
                    _uiState.value = UserUiState.Success(
                        user = updatedUser,
                        isEditing = false
                    )
                    _events.send("Profile updated successfully")
                }
                .onFailure { error ->
                    _uiState.value = currentState.copy(
                        editState = currentState.editState.copy(
                            isSaving = false,
                            saveError = error.message ?: "Failed to update profile"
                        )
                    )
                }
        }
    }

    private fun validateFields(state: EditProfileUiState): Boolean {
        var isValid = true
        var firstNameError: String? = null
        var lastNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null

        if (state.firstName.isBlank()) {
            firstNameError = "First name cannot be empty"
            isValid = false
        }

        if (state.lastName.isBlank()) {
            lastNameError = "Last name cannot be empty"
            isValid = false
        }

        if (state.email.isBlank()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        }

        if (state.password.isNotEmpty() && state.password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        if (!isValid) {
            updateEditState {
                it.copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
        }

        return isValid
    }
}
