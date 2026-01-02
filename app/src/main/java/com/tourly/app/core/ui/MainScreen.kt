package com.tourly.app.core.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.chat.presentation.ui.ChatScreen
import com.tourly.app.core.ui.components.BottomNavDestination
import com.tourly.app.core.ui.components.BottomNavigationBar
import com.tourly.app.core.ui.components.SimpleTopBar
import com.tourly.app.dashboard.presentation.ui.DashboardScreen
import com.tourly.app.home.presentation.ui.HomeScreen
import com.tourly.app.profile.presentation.ui.ProfileScreen
import com.tourly.app.core.ui.utils.WindowSizeState

@Composable
fun MainScreen(
    windowSizeState: WindowSizeState,
    onLogout: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(BottomNavDestination.HOME)
    }

    var isEditingProfile by rememberSaveable { mutableStateOf(false) }
    var onCancelEdit: (() -> Unit)? by remember { mutableStateOf(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopBar(
                title = if (isEditingProfile) {
                    stringResource(id = R.string.edit_profile)
                } else {
                    selectedDestination.label
                },
                navigationIcon = {
                    if (isEditingProfile) {
                        IconButton(onClick = { onCancelEdit?.invoke() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    if (selectedDestination == BottomNavDestination.PROFILE && !isEditingProfile) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(id = R.string.settings)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isEditingProfile) {
                BottomNavigationBar(
                    selectedDestination = selectedDestination,
                    onDestinationSelected = { destination ->
                        selectedDestination = destination
                    },
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        when (selectedDestination) {
            BottomNavDestination.HOME -> {
                HomeScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            BottomNavDestination.DASHBOARD -> {
                DashboardScreen(
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
                    snackbarHostState = snackbarHostState,
                    onLogout = onLogout,
                    onNavigateToSettings = onNavigateToSettings,
                    onEditingStateChange = { isEditing, cancelCallback ->
                        isEditingProfile = isEditing
                        onCancelEdit = cancelCallback
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
