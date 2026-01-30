package com.tourly.app.core.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.ui.components.BottomNavDestination
import com.tourly.app.core.presentation.ui.utils.WindowSizeState
import com.tourly.app.core.presentation.viewmodel.UserViewModel

@Composable
fun GuideMainScreen(
    windowSizeState: WindowSizeState,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onTourClick: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel()
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(BottomNavDestination.GUIDE_HOME)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        userViewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    GuideMainContent(
        modifier = modifier,
        selectedDestination = selectedDestination,
        snackbarHostState = snackbarHostState,
        onDestinationSelected = { destination ->
            selectedDestination = destination
        },
        onNavigateToSettings = onNavigateToSettings,
        onLogout = onLogout,
        onTourClick = onTourClick,
        onEditTour = onEditTour,
        onNavigateToNotifications = onNavigateToNotifications,
        userViewModel = userViewModel
    )
}
