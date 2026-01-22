package com.tourly.app.create_tour.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.viewmodel.CreateTourViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateTourScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit = {},
    viewModel: CreateTourViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateTourEvent.Success -> {
                    snackbarHostState.showSnackbar(
                        message = "Tour created successfully!",
                        duration = SnackbarDuration.Short
                    )
                    onNavigateBack()

                    // TODO: Navigate
                }
                is CreateTourEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    CreateTourContent(
        modifier = modifier,
        state = uiState,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onLocationChanged = viewModel::onLocationChanged,
        onDurationChanged = viewModel::onDurationChanged,
        onMaxGroupSizeChanged = viewModel::onMaxGroupSizeChanged,
        onPriceChanged = viewModel::onPriceChanged,
        onWhatsIncludedChanged = viewModel::onWhatsIncludedChanged,
        onScheduledDateChanged = viewModel::onScheduledDateChanged,
        onImageSelected = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCreateTour = viewModel::onCreateTour
    )
}