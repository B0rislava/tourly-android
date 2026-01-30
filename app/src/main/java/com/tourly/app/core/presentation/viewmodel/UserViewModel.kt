package com.tourly.app.core.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.usecase.CancelBookingUseCase
import com.tourly.app.core.domain.usecase.GetMyBookingsUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateProfilePictureUseCase
import com.tourly.app.core.domain.usecase.DeleteAccountUseCase
import com.tourly.app.create_tour.domain.usecase.GetMyToursUseCase
import com.tourly.app.create_tour.domain.usecase.DeleteTourUseCase
import com.tourly.app.core.network.model.UpdateProfileRequestDto
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.network.Result
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.state.EditProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val tokenManager: TokenManager,
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val getMyToursUseCase: GetMyToursUseCase,
    private val deleteTourUseCase: DeleteTourUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    fun showMessage(message: String) {
        viewModelScope.launch {
            _events.send(message)
        }
    }

    init {
        observeToken()
    }

    private fun observeToken() {
        viewModelScope.launch {
            tokenManager.getTokenFlow()
                .distinctUntilChanged()
                .collect { token ->
                    if (token != null) {
                        val currentState = _uiState.value
                        if (currentState !is UserUiState.Success || currentState.user.email == "") {
                             fetchUserProfile()
                        }
                    } else {
                        _uiState.value = UserUiState.Idle
                    }
                }
        }
    }

    private suspend fun fetchUserProfile() {
        val currentState = _uiState.value
        if (currentState !is UserUiState.Success) {
            _uiState.value = UserUiState.Loading
        }
        
        when (val result = getUserProfileUseCase()) {
            is Result.Success -> {
                val user = result.data
                _uiState.value = when (val current = _uiState.value) {
                    is UserUiState.Success -> current.copy(
                        user = user,
                        bookings = if (user.role == UserRole.TRAVELER) current.bookings else emptyList(),
                        tours = if (user.role == UserRole.GUIDE) current.tours else emptyList()
                    )
                    else -> UserUiState.Success(user)
                }
                
                if (user.role == UserRole.TRAVELER) {
                    fetchBookings()
                } else if (user.role == UserRole.GUIDE) {
                    fetchTours()
                }
            }
            is Result.Error -> {
                _uiState.value = UserUiState.Error(result.message)
            }
        }
    }

    fun refreshBookings() {
        viewModelScope.launch {
            // If we're already success, just fetch
            val currentState = _uiState.value
            if (currentState is UserUiState.Success) {
                if (currentState.user.role == UserRole.TRAVELER) {
                    fetchBookings()
                } else {
                    fetchTours()
                }
            } else {
                // If not success, try full profile fetch which will fetch specific data too
                fetchUserProfile()
            }
        }
    }

    private suspend fun fetchBookings() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            when (val result = getMyBookingsUseCase()) {
                is Result.Success -> {
                    val current = _uiState.value
                    if (current is UserUiState.Success) {
                        _uiState.value = current.copy(bookings = result.data)
                    }
                }
                is Result.Error -> {
                    // TODO: Handle error
                }
            }
        }
    }

    private suspend fun fetchTours() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            when (val result = getMyToursUseCase()) {
                is Result.Success -> {
                    val current = _uiState.value
                    if (current is UserUiState.Success) {
                        _uiState.value = current.copy(tours = result.data)
                    }
                }
                is Result.Error -> {
                    // TODO: Handle error
                }
            }
        }
    }

    fun cancelBooking(id: Long) {
        viewModelScope.launch {
            when (val result = cancelBookingUseCase(id)) {
                is Result.Success -> {
                    _events.send("Booking cancelled successfully")
                    fetchBookings()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }

    fun deleteTour(id: Long) {
        viewModelScope.launch {
            when (val result = deleteTourUseCase(id)) {
                is Result.Success -> {
                    _events.send("Tour deleted successfully")
                    fetchTours()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            when (val result = deleteAccountUseCase()) {
                is Result.Success -> {
                    _events.send("Account deleted successfully")
                    onSuccess()
                }
                is Result.Error -> {
                    _events.send(result.message)
                    fetchUserProfile()
                }
            }
        }
    }

    fun startEditing() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            _uiState.value = currentState.copy(
                isEditing = true,
                editState = EditProfileUiState(
                    fullName = "${currentState.user.firstName} ${currentState.user.lastName}".trim(),
                    email = currentState.user.email,
                    bio = currentState.user.bio ?: "",
                    certifications = currentState.user.certifications ?: "",
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

    fun onFullNameChange(value: String) {
        updateEditState { it.copy(fullName = value, fullNameError = null) }
    }

    fun onEmailChange(value: String) {
        updateEditState { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        updateEditState { it.copy(password = value, passwordError = null) }
    }

    fun onBioChange(value: String) {
        updateEditState { it.copy(bio = value, bioError = null) }
    }

    fun onCertificationsChange(value: String) {
        updateEditState { it.copy(certifications = value, certificationsError = null) }
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
                        val result = updateProfilePictureUseCase(bytes)
                        if (result is Result.Error) {
                            uploadError = "Failed to upload image: ${result.message}"
                        }
                    } else {
                        uploadError = "Failed to read image file"
                    }
                } catch (e: Exception) {
                    uploadError = "Error processing image: ${e.message}"
                }
            }

            if (uploadError != null) {
                val current = _uiState.value
                if (current is UserUiState.Success) {
                    _uiState.value = current.copy(
                        editState = current.editState.copy(
                            isSaving = false,
                            saveError = uploadError
                        )
                    )
                }
                return@launch
            }

            val names = currentState.editState.fullName.trim().split(" ", limit = 2)
            val firstName = names.getOrNull(0) ?: ""
            val lastName = names.getOrNull(1) ?: ""

            val request = UpdateProfileRequestDto(
                email = currentState.editState.email,
                firstName = firstName,
                lastName = lastName,
                bio = currentState.editState.bio.ifBlank { null },
                certifications = currentState.editState.certifications.ifBlank { null },
                password = currentState.editState.password.ifBlank { null }
            )

            when (val result = updateUserProfileUseCase(request)) {
                is Result.Success -> {
                    // Update state to success and close editing
                    val current = _uiState.value
                    if (current is UserUiState.Success) {
                        _uiState.value = current.copy(
                            user = result.data,
                            isEditing = false,
                            editState = EditProfileUiState() // Reset edit state
                        )
                    } else {
                        _uiState.value = UserUiState.Success(
                            user = result.data,
                            isEditing = false
                        )
                    }
                    _events.send("Profile updated successfully")
                }
                is Result.Error -> {
                    val current = _uiState.value
                    if (current is UserUiState.Success) {
                        _uiState.value = current.copy(
                            editState = current.editState.copy(
                                isSaving = false,
                                saveError = result.message
                            )
                        )
                    }
                }
            }
        }
    }

    private fun validateFields(state: EditProfileUiState): Boolean {
        var isValid = true
        var fullNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null

        if (state.fullName.isBlank()) {
            fullNameError = "Name cannot be empty"
            isValid = false
        } else if (!state.fullName.trim().contains(" ")) {
            fullNameError = "Please enter both first and last name"
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
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
        }

        return isValid
    }
}
