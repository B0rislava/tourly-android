package com.tourly.app.core.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.tourly.app.core.presentation.ui.components.BottomNavDestination.Companion.guideDestinations
import com.tourly.app.core.presentation.ui.components.BottomNavigationBar
import com.tourly.app.core.presentation.ui.components.SimpleTopBar
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.dashboard.presentation.ui.DashboardScreen
import com.tourly.app.profile.presentation.ui.ProfileScreen
import com.tourly.app.home.presentation.ui.HomeScreen
import com.tourly.app.create_tour.presentation.ui.CreateTourScreen

@Composable
fun GuideMainContent(
    modifier: Modifier = Modifier,
    selectedDestination: BottomNavDestination,
    snackbarHostState: SnackbarHostState,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    onTourClick: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    userViewModel: UserViewModel
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {

            if (selectedDestination != BottomNavDestination.GUIDE_HOME) {
                SimpleTopBar(
                    title = selectedDestination.label,
                    navigationIcon = {
                        val onBack: (() -> Unit)? = when {
                            selectedDestination == BottomNavDestination.CREATE_TOUR -> {
                                { onDestinationSelected(BottomNavDestination.GUIDE_HOME) }
                            }
                            else -> null
                        }

                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    },
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
                destinations = guideDestinations,
                onDestinationSelected = onDestinationSelected,
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (selectedDestination) {
            BottomNavDestination.GUIDE_HOME -> {
                HomeScreen(
                    onSessionExpired = onLogout,
                    onTourClick = onTourClick,
                    onNotifyClick = onNavigateToNotifications,
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                )
            }
            BottomNavDestination.GUIDE_DASHBOARD -> {
                LaunchedEffect(Unit) {
                    userViewModel.refreshBookings()
                }
                DashboardScreen(
                    userViewModel = userViewModel,
                    onEditTour = onEditTour,
                    onCreateTour = { onDestinationSelected(BottomNavDestination.CREATE_TOUR) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavDestination.CREATE_TOUR -> {
                CreateTourScreen(
                    snackbarHostState = snackbarHostState,
                    onCreateTourSuccess = { userViewModel.refreshBookings() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavDestination.CHAT -> {
                ChatScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavDestination.PROFILE -> {
                ProfileScreen(
                    onSeeMore = { onDestinationSelected(BottomNavDestination.GUIDE_DASHBOARD) },
                    userViewModel = userViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {}
        }
    }
}
