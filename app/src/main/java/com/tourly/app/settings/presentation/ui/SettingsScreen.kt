package com.tourly.app.settings.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.domain.model.User
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.settings.presentation.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onNavigatePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val user by viewModel.user.collectAsState()

    // Refresh user data when returning to this screen
    LaunchedEffect(Unit) {
        viewModel.refreshUserProfile()
    }

    SettingsContent(
        user = user,
        themeMode = themeMode,
        onNavigateBack = onNavigateBack,
        onSetThemeMode = { viewModel.setThemeMode(it) },
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
        onNavigateBack = {},
        onSetThemeMode = {},
        onLogout = {},
        onNavigateProfileDetails = {},
        onNavigatePassword = {},
        onNavigateNotifications = {},
        onDeleteAccount = {}
    )
}
