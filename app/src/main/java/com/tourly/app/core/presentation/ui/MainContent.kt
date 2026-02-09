package com.tourly.app.core.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.chat.presentation.ui.ChatScreen
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.ui.components.BottomNavDestination
import com.tourly.app.core.presentation.ui.components.BottomNavDestination.Companion.travelerDestinations
import com.tourly.app.core.presentation.ui.components.BottomNavigationBar
import com.tourly.app.core.presentation.ui.components.LoadingScreen
import com.tourly.app.core.presentation.ui.components.SimpleTopBar
import com.tourly.app.dashboard.presentation.ui.DashboardScreen
import com.tourly.app.home.presentation.ui.HomeScreen
import com.tourly.app.profile.presentation.ui.ProfileScreen
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.home.presentation.ui.components.ErrorState

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    selectedDestination: BottomNavDestination,
    snackbarHostState: SnackbarHostState,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    onTourClick: (Long) -> Unit,
    onChatClick: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    userViewModel: UserViewModel
) {
    val userUiState by userViewModel.uiState.collectAsState()
    
    when (val state = userUiState) {
        is UserUiState.Loading,
        is UserUiState.Idle -> {
            LoadingScreen()
        }
        is UserUiState.Error -> {
            ErrorState(
                message = state.message,
                errorCode = "CONNECTION_ERROR",
                onRetry = { userViewModel.refreshBookings(forceOwnProfile = true) },
                modifier = Modifier.fillMaxSize()
            )
        }
        is UserUiState.Success -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (selectedDestination != BottomNavDestination.TRAVELER_HOME &&
                    selectedDestination != BottomNavDestination.TRAVELER_DASHBOARD &&
                    selectedDestination != BottomNavDestination.CHAT
                ) {
                    SimpleTopBar(
                        title = stringResource(id = selectedDestination.labelResId),
                        actions = {
                            if (selectedDestination == BottomNavDestination.PROFILE) {
                                IconButton(onClick = onNavigateToSettings) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = stringResource(id = R.string.settings)
                                    )
                                }
                            }
                        }
                    )
                }
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedDestination = selectedDestination,
                    destinations = travelerDestinations,
                    onDestinationSelected = onDestinationSelected,
                )
            },
            modifier = modifier
        ) { paddingValues ->
            when (selectedDestination) {
                BottomNavDestination.TRAVELER_HOME -> {
                    HomeScreen(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                        onSessionExpired = onLogout,
                        onTourClick = onTourClick,
                        onNotifyClick = onNavigateToNotifications
                    )
                }
                BottomNavDestination.TRAVELER_DASHBOARD -> {
                    DashboardScreen(
                        onEditTour = {},
                        onCreateTour = {},
                        onTourClick = onTourClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                BottomNavDestination.CHAT -> {
                    ChatScreen(
                        onChatClick = onChatClick,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                BottomNavDestination.PROFILE -> {
                    ProfileScreen(
                        userViewModel = userViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                else -> {}
            }
        }
        }
    }
}
