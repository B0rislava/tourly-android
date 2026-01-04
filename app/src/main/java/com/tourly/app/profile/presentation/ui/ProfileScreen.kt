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
    onEditingStateChange: (Boolean, (() -> Unit)?) -> Unit
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
                        onPasswordChange = userViewModel::onPasswordChange,
                        onProfilePictureSelected = userViewModel::onProfilePictureSelected,
                        onSaveClick = userViewModel::saveProfile
                    )
                } else {
                    ProfileContent(
                        firstName = state.user.firstName,
                        lastName = state.user.lastName,
                        email = state.user.email,
                        role = state.user.role,
                        profilePictureUrl = state.user.profilePictureUrl,
                        onLogout = onLogout,
                        onEditProfile = userViewModel::startEditing
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
private fun ProfileContentPreview() {
    ProfileContent(
        firstName = "Ashley",
        lastName = "Watson",
        email = "ashley.watson@example.com",
        role = UserRole.GUIDE,
        profilePictureUrl = null,
        onLogout = {},
        onEditProfile = {}
    )
}