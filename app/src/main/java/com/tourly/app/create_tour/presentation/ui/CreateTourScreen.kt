package com.tourly.app.create_tour.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.ui.components.CropShape
import com.tourly.app.core.presentation.ui.components.ImageCropperDialog
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.viewmodel.CreateTourViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateTourScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit = {},
    onCreateTourSuccess: () -> Unit = {},
    viewModel: CreateTourViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val addressPredictions by viewModel.addressPredictions.collectAsState()

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    if (pendingImageUri != null) {
        ImageCropperDialog(
            imageUri = pendingImageUri!!,
            cropShape = CropShape.Rectangle,
            aspectRatio = 1.6f,
            title = "Crop Tour Image",
            onCrop = { croppedUri ->
                viewModel.onImageSelected(croppedUri)
                pendingImageUri = null
            },
            onDismiss = {
                pendingImageUri = null
            }
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingImageUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateTourEvent.Success -> {
                    snackbarHostState.showSnackbar(
                        message = "Tour created successfully!",
                        duration = SnackbarDuration.Short
                    )
                    onCreateTourSuccess()
                    onNavigateBack()
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
        onDurationChanged = viewModel::onDurationChanged,
        onMaxGroupSizeChanged = viewModel::onMaxGroupSizeChanged,
        onPriceChanged = viewModel::onPriceChanged,
        onWhatsIncludedChanged = viewModel::onWhatsIncludedChanged,
        onScheduledDateChanged = viewModel::onScheduledDateChanged,
        onStartTimeChanged = viewModel::onStartTimeChanged,
        onTagToggled = viewModel::onTagToggled,
        onImageSelected = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onLocationPredictionClick = viewModel::onLocationSelected,
        addressPredictions = addressPredictions,
        onMeetingPointAddressChanged = viewModel::onMeetingPointAddressChanged,
        onMeetingPointSelected = viewModel::onMeetingPointSelected,
        onCreateTour = viewModel::onCreateTour
    )
}