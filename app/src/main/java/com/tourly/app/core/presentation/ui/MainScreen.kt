package com.tourly.app.core.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tourly.app.core.presentation.ui.components.BottomNavDestination
import com.tourly.app.core.presentation.ui.utils.WindowSizeState

@Composable
fun MainScreen(
    windowSizeState: WindowSizeState,
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onTourClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(BottomNavDestination.TRAVELER_HOME)
    }

    var isEditingProfile by rememberSaveable { mutableStateOf(false) }
    var onCancelEdit: (() -> Unit)? by remember { mutableStateOf(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    MainContent(
        modifier = modifier,
        selectedDestination = selectedDestination,
        isEditingProfile = isEditingProfile,
        onCancelEdit = { onCancelEdit?.invoke() },
        snackbarHostState = snackbarHostState,
        onDestinationSelected = { destination ->
            selectedDestination = destination
        },
        onNavigateToSettings = onNavigateToSettings,
        onLogout = onLogout,
        onAccountDeleted = onAccountDeleted,
        onTourClick = onTourClick,
        onNavigateToNotifications = onNavigateToNotifications,
        onEditingStateChange = { isEditing, cancelCallback ->
            isEditingProfile = isEditing
            onCancelEdit = cancelCallback
        }
    )
}
