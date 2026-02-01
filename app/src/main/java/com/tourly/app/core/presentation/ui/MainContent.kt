package com.tourly.app.core.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.chat.presentation.ui.ChatScreen
import com.tourly.app.core.presentation.ui.components.BottomNavDestination
import com.tourly.app.core.presentation.ui.components.BottomNavDestination.Companion.travelerDestinations
import com.tourly.app.core.presentation.ui.components.BottomNavigationBar
import com.tourly.app.core.presentation.ui.components.SimpleTopBar
import com.tourly.app.dashboard.presentation.ui.DashboardScreen
import com.tourly.app.home.presentation.ui.HomeScreen
import com.tourly.app.profile.presentation.ui.ProfileScreen
import com.tourly.app.core.presentation.viewmodel.UserViewModel

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
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedDestination != BottomNavDestination.TRAVELER_HOME) {
                SimpleTopBar(
                    title = selectedDestination.label,
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
                // If TopBar is hidden, paddingValues top is 0. 
                // We pass paddingValues to handle BottomBar.
                HomeScreen(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()), // Apply only bottom padding, let Home manage top
                    onSessionExpired = onLogout,
                    onTourClick = onTourClick,
                    onNotifyClick = onNavigateToNotifications
                )
            }
            BottomNavDestination.TRAVELER_DASHBOARD -> {
                LaunchedEffect(Unit) {
                    userViewModel.refreshBookings()
                }
                DashboardScreen(
                    userViewModel = userViewModel,
                    onEditTour = {}, // Travelers don't edit tours
                    onCreateTour = {}, // Travelers don't create tours
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
                    onSeeMore = { onDestinationSelected(BottomNavDestination.TRAVELER_DASHBOARD) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {}
        }
    }
}
