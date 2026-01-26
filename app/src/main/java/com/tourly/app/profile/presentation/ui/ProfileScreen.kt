package com.tourly.app.profile.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit,
    onEditingStateChange: (Boolean, (() -> Unit)?) -> Unit,
    onEditTour: (Long) -> Unit = {}
) {
    val userState by userViewModel.uiState.collectAsState()

    // Notify parent about editing state changes
    LaunchedEffect(userState) {
        when (val state = userState) {
            is UserUiState.Success -> {
                onEditingStateChange(state.isEditing, userViewModel::cancelEditing)
            }
            else -> {
                onEditingStateChange(false, null)
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.refreshBookings()
    }

    LaunchedEffect(key1 = true) {
        userViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = userState) {
            is UserUiState.Idle -> {
                Text(
                    text = "No user data available",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = OutfitFamily
                )
            }
            is UserUiState.Loading -> {
                CircularProgressIndicator()
            }
            is UserUiState.Success -> {
                if (state.isEditing) {
                    BackHandler {
                        userViewModel.cancelEditing()
                    }

                    EditProfileContent(
                        state = state.editState,
                        onFirstNameChange = userViewModel::onFirstNameChange,
                        onLastNameChange = userViewModel::onLastNameChange,
                        onEmailChange = userViewModel::onEmailChange,
                        onBioChange = userViewModel::onBioChange,
                        onCertificationsChange = userViewModel::onCertificationsChange,
                        onPasswordChange = userViewModel::onPasswordChange,
                        onProfilePictureSelected = userViewModel::onProfilePictureSelected,
                        onSaveClick = userViewModel::saveProfile
                    )
                } else {
                    ProfileContent(
                        user = state.user,
                        onLogout = onLogout,
                        onEditProfile = userViewModel::startEditing,
                        onEditTour = onEditTour,
                        onDeleteTour = userViewModel::deleteTour,
                        onCancelBooking = userViewModel::cancelBooking,
                        bookings = state.bookings,
                        tours = state.tours
                    )
                }
            }
            is UserUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideProfileContentPreview() {
    ProfileContent(
        user = User(
            id = 1,
            email = "guide@example.com",
            firstName = "John",
            lastName = "Guide",
            role = UserRole.GUIDE,
            profilePictureUrl = null,
            bio = "Experienced tour guide in Sofia.",
            rating = 4.8,
            reviewsCount = 120,
            followerCount = 450,
            toursCompleted = 85
        ),
        onLogout = {},
        onEditProfile = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TravelerProfileContentPreview() {
    ProfileContent(
        user = User(
            id = 2,
            email = "traveler@example.com",
            firstName = "Jane",
            lastName = "Traveler",
            role = UserRole.TRAVELER,
            profilePictureUrl = null
        ),
        onLogout = {},
        onEditProfile = {}
    )
}