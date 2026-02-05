package com.tourly.app.settings.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.domain.model.AppLanguage
import com.tourly.app.core.domain.model.User
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.settings.presentation.viewmodel.SettingsViewModel

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.tourly.app.core.presentation.viewmodel.UserEvent
import com.tourly.app.core.presentation.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onNavigatePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val user by viewModel.user.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Observe shared user events (like "Profile updated successfully")
    LaunchedEffect(Unit) {
        userViewModel.events.collect { event ->
            val message = when (event) {
                is UserEvent.Message -> event.message
                is UserEvent.ResourceMessage -> context.applicationContext.getString(event.resId)
                is UserEvent.Success -> null
            }
            message?.let { snackbarHostState.showSnackbar(it) }
        }
    }


    // Refresh user data when returning to this screen
    LaunchedEffect(Unit) {
        viewModel.refreshUserProfile()
    }

    SettingsContent(
        user = user,
        themeMode = themeMode,
        currentLanguage = currentLanguage,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onSetThemeMode = { viewModel.setThemeMode(it) },
        onSetLanguage = { viewModel.setLanguage(it) },
        onLogout = { viewModel.logout(onLogout) },
        onNavigateProfileDetails = onNavigateToEditProfile,
        onNavigatePassword = onNavigatePassword,
        onNavigateNotifications = { },
        onDeleteAccount = { viewModel.deleteAccount(onAccountDeleted) }
    )
}





@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsContent(
        user = User(
            id = 1,
            email = "jane@example.com",
            firstName = "Jane",
            lastName = "Cooper",
            role = UserRole.TRAVELER,
            profilePictureUrl = null
        ),
        themeMode = ThemeMode.SYSTEM,
        currentLanguage = AppLanguage.ENGLISH,
        onNavigateBack = {},
        onSetThemeMode = {},
        onSetLanguage = {},
        onLogout = {},
        onNavigateProfileDetails = {},
        onNavigatePassword = {},
        onNavigateNotifications = {},
        onDeleteAccount = {}
    )
}
