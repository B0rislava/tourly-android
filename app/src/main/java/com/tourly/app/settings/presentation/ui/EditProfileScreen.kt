package com.tourly.app.settings.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.R
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.profile.presentation.ui.EditProfileContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val userState by userViewModel.uiState.collectAsState()

    // Enter edit mode when screen opens
    LaunchedEffect(Unit) {
        userViewModel.startEditing()
    }

    // Exit edit mode when screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            userViewModel.cancelEditing()
        }
    }

    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_profile),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = userState) {
                is UserUiState.Success -> {
                    EditProfileContent(
                        state = state.editState,
                        onFullNameChange = userViewModel::onFullNameChange,
                        onEmailChange = userViewModel::onEmailChange,
                        onBioChange = userViewModel::onBioChange,
                        onCertificationsChange = userViewModel::onCertificationsChange,
                        onProfilePictureSelected = userViewModel::onProfilePictureSelected,
                        onSaveClick = {
                            userViewModel.saveProfile()
                            onNavigateBack()
                        }
                    )
                }
                is UserUiState.Loading -> {
                    // Show loading state if needed
                }
                is UserUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}
