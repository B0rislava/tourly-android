package com.tourly.app.core.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileByIdUseCase
import com.tourly.app.core.domain.usecase.UpdateUserProfileUseCase
import com.tourly.app.core.domain.usecase.UpdateProfilePictureUseCase
import com.tourly.app.core.domain.usecase.ObserveAuthStateUseCase
import com.tourly.app.core.domain.usecase.ToggleFollowUseCase
import com.tourly.app.create_tour.domain.usecase.GetMyToursUseCase
import com.tourly.app.create_tour.domain.usecase.GetUserToursUseCase
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.network.Result
import com.tourly.app.profile.data.dto.UpdateProfileRequestDto
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.state.EditProfileUiState
import com.tourly.app.core.domain.usecase.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import com.tourly.app.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserProfileByIdUseCase: GetUserProfileByIdUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val getMyToursUseCase: GetMyToursUseCase,
    private val getUserToursUseCase: GetUserToursUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UserEvent>()
    val events: SharedFlow<UserEvent> = _events.asSharedFlow()

    fun showMessage(message: String) {
        viewModelScope.launch {
            _events.emit(UserEvent.Message(message))
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
                        // Observe the global user flow via UseCase
                        launch {
                            observeUserProfileUseCase().collect { profile ->
                                if (profile != null) {
                                    val currentState = _uiState.value
                                    if (currentState is UserUiState.Success && currentState.isOwnProfile) {
                                        _uiState.value = currentState.copy(user = profile)
                                    } else {
                                        // Initialize Success state if not already there and we have user data
                                        _uiState.value = UserUiState.Success(
                                            user = profile,
                                            isOwnProfile = true
                                        )
                                    }
                                } else {
                                    // If logged in but no profile yet, trigger fetch
                                    fetchUserProfile()
                                }
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
                if (currentState.user.role == UserRole.GUIDE) {
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
                    tours = if (user.role == UserRole.GUIDE) (currentState as? UserUiState.Success)?.tours ?: emptyList() else emptyList()
                )
                
                if (user.role == UserRole.GUIDE) {
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



    fun toggleFollow() {
        val currentState = _uiState.value
        if (currentState !is UserUiState.Success) return
        val user = currentState.user

        viewModelScope.launch {
            // Optimistic update
            val newIsFollowing = !user.isFollowing
            val newCount = if (newIsFollowing) user.followerCount + 1 else user.followerCount - 1
            
            _uiState.value = currentState.copy(
                user = user.copy(
                    isFollowing = newIsFollowing,
                    followerCount = newCount.coerceAtLeast(0)
                )
            )

            val result = toggleFollowUseCase(user.id, newIsFollowing)

            if (result is Result.Error) {
                // Revert logic could be simpler or just reload
                _uiState.value = currentState // Revert to original state
                _events.emit(UserEvent.Message("Failed to update follow status: ${result.message}"))
            }
        }
    }



    fun startEditing() {
        val currentState = _uiState.value
        if (currentState is UserUiState.Success) {
            _uiState.value = currentState.copy(
                isEditing = true,
                editState = EditProfileUiState(
                    userRole = currentState.user.role,
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
                isSavingAvatar = currentState.editState.profilePictureUri != null,
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
                        isSavingAvatar = false,
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
                            isSavingAvatar = false,
                            // Keep editState as is so fields don't disappear during transition
                        )
                    } else {
                        _uiState.value = UserUiState.Success(
                            user = result.data,
                            isEditing = false,
                            isSavingAvatar = false
                        )
                    }
                    _events.emit(UserEvent.ResourceMessage(R.string.profile_updated_success))
                    _events.emit(UserEvent.Success)
                }
                is Result.Error -> {
                    val current = _uiState.value
                    if (current is UserUiState.Success) {
                        _uiState.value = current.copy(
                            isSavingAvatar = false,
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
        var fullNameError: Int? = null
        var emailError: Int? = null
        var passwordError: Int? = null
        var confirmPasswordError: Int? = null

        if (state.fullName.isBlank()) {
            fullNameError = R.string.error_name_empty
            isValid = false
        } else if (!state.fullName.trim().contains(" ")) {
            fullNameError = R.string.error_name_invalid
            isValid = false
        }

        if (state.email.isBlank()) {
            emailError = R.string.error_email_empty
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailError = R.string.error_email_invalid
            isValid = false
        }

        if (state.password.isNotEmpty()) {
            if (state.password.length < 6) {
                passwordError = R.string.error_password_too_short
                isValid = false
            }
            if (state.password != state.confirmPassword) {
                confirmPasswordError = R.string.error_passwords_dont_match
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

sealed class UserEvent {
    data class Message(val message: String) : UserEvent()
    data class ResourceMessage(val resId: Int) : UserEvent()
    object Success : UserEvent()
}
