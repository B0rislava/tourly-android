package com.tourly.app.settings.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.ui.components.SimpleTopBar
import com.tourly.app.core.presentation.viewmodel.UserViewModel

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.startEditing()
    }
    
    DisposableEffect(Unit) {
        onDispose { userViewModel.cancelEditing() }
    }

    // Observe events for success navigation
    LaunchedEffect(Unit) {
        userViewModel.events.collect { event ->
            if (event == "Profile updated successfully") {
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "Change Password",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when(val state = userState) {
            is UserUiState.Success -> {
                ChangePasswordContent(
                    state = state.editState,
                    onPasswordChange = userViewModel::onPasswordChange,
                    onConfirmPasswordChange = userViewModel::onConfirmPasswordChange,
                    onSaveClick = userViewModel::saveProfile,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {}
        }
    }
}
