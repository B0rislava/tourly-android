package com.tourly.app.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.usecase.CancelBookingUseCase
import com.tourly.app.core.domain.usecase.GetMyBookingsUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileByIdUseCase
import com.tourly.app.core.domain.usecase.UpdateUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateProfilePictureUseCase
import com.tourly.app.create_tour.domain.usecase.GetMyToursUseCase
import com.tourly.app.create_tour.domain.usecase.GetUserToursUseCase
import com.tourly.app.create_tour.domain.usecase.DeleteTourUseCase
import com.tourly.app.profile.data.dto.UpdateProfileRequestDto
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.network.Result
import com.tourly.app.core.domain.usecase.ObserveAuthStateUseCase
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.state.EditProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserProfileByIdUseCase: GetUserProfileByIdUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val getMyToursUseCase: GetMyToursUseCase,
    private val getUserToursUseCase: GetUserToursUseCase,
    private val deleteTourUseCase: DeleteTourUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val savedStateHandle: SavedStateHandle
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
        val userId = savedStateHandle.get<Long>("userId")
        if (userId != null && userId != -1L) {
             fetchOtherUserProfile(userId)
        } else {
            observeToken()
        }
    }

    private fun observeToken() {
        viewModelScope.launch {
            observeAuthStateUseCase()
                .distinctUntilChanged()
                .collect { isLoggedIn ->
                    if (isLoggedIn) {
                        val currentState = _uiState.value
                        if (currentState !is UserUiState.Success || currentState.isOwnProfile) {
                             if (currentState !is UserUiState.Success || currentState.user.email == "") {
                                 fetchUserProfile()
                             }
                        }
                    } else {
                        _uiState.value = UserUiState.Idle
                    }
                }
        }
    }


    fun refreshBookings(forceOwnProfile: Boolean = false) {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            // If we force own profile or aren't in a success state yet, fetch appropriate profile
            if (forceOwnProfile || currentState !is UserUiState.Success) {
                val userId = savedStateHandle.get<Long>("userId")
                if (userId != null && userId != -1L && !forceOwnProfile) {
                    fetchOtherUserProfile(userId)
                } else {
                    fetchUserProfile()
                }
                return@launch
            }

            if (currentState.isOwnProfile) {
                if (currentState.user.role == UserRole.TRAVELER) {
                    fetchBookings()
                } else {
                    fetchTours()
                }
            } else {
                // Fetch other user's tours if they are a guide
                if (currentState.user.role == UserRole.GUIDE) {
                    fetchOtherUserTours(currentState.user.id)
                }
            }
        }
    }

    private suspend fun fetchUserProfile() {
        val currentState = _uiState.value
        // If we are currently showing someone else's profile, we MUST reload everything
        if (currentState !is UserUiState.Success || !currentState.isOwnProfile) {
            _uiState.value = UserUiState.Loading
        }
        
        when (val result = getUserProfileUseCase()) {
            is Result.Success -> {
                val user = result.data
                _uiState.value = UserUiState.Success(
                    user = user,
                    isOwnProfile = true, // Always true for this method
                    bookings = if (user.role == UserRole.TRAVELER) (currentState as? UserUiState.Success)?.bookings ?: emptyList() else emptyList(),
                    tours = if (user.role == UserRole.GUIDE) (currentState as? UserUiState.Success)?.tours ?: emptyList() else emptyList()
                )
                
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

    fun fetchOtherUserProfile(userId: Long) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            when (val result = getUserProfileByIdUseCase(userId)) {
                is Result.Success -> {
                    val user = result.data
                    _uiState.value = UserUiState.Success(
                        user = user,
                        isOwnProfile = false
                    )
                    if (user.role == UserRole.GUIDE) {
                        fetchOtherUserTours(userId)
                    }
                }
                is Result.Error -> {
                    _uiState.value = UserUiState.Error(result.message)
                }
            }
        }
    }

    private suspend fun fetchOtherUserTours(userId: Long) {
        when (val result = getUserToursUseCase(userId)) {
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
        updateEditState { it.copy(password = value, passwordError = null, confirmPasswordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        updateEditState { it.copy(confirmPassword = value, confirmPasswordError = null) }
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

            // Upload profile picture if changed
            var uploadError: String? = null
            if (currentState.editState.profilePictureUri != null) {
                val result = updateProfilePictureUseCase(currentState.editState.profilePictureUri.toString())
                if (result is Result.Error) {
                    uploadError = "Failed to upload image: ${result.message}"
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
        var confirmPasswordError: String? = null

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

        if (state.password.isNotEmpty()) {
            if (state.password.length < 6) {
                passwordError = "Password must be at least 6 characters"
                isValid = false
            }
            if (state.password != state.confirmPassword) {
                confirmPasswordError = "Passwords do not match"
                isValid = false
            }
        }

        if (!isValid) {
            updateEditState {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
        }

        return isValid
    }
}
