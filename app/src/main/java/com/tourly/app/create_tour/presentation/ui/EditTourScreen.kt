package com.tourly.app.create_tour.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.ui.components.CropShape
import com.tourly.app.core.presentation.ui.components.ImageCropperDialog
import com.tourly.app.core.presentation.ui.components.SimpleTopBar
import com.tourly.app.create_tour.presentation.util.CreateTourEvent
import com.tourly.app.create_tour.presentation.viewmodel.EditTourViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditTourScreen(
    tourId: Long,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onUpdateSuccess: () -> Unit = {},
    viewModel: EditTourViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.setTourId(tourId)
    }

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
                    onUpdateSuccess()
                    onNavigateBack()
                }
                is CreateTourEvent.Error -> {
                    // We might still want to show errors here since we are still on the screen
                    // But for consistency with Option B, we could also pass this back.
                    // Let's keep a local error snackbar for now or just the navigation back.
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SimpleTopBar(
                title = "Edit Tour",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        CreateTourContent(
            modifier = modifier.padding(paddingValues),
            state = uiState,
            onTitleChanged = viewModel::onTitleChanged,
            onDescriptionChanged = viewModel::onDescriptionChanged,
            onDurationChanged = viewModel::onDurationChanged,
            onMaxGroupSizeChanged = viewModel::onMaxGroupSizeChanged,
            onPriceChanged = viewModel::onPriceChanged,
            onWhatsIncludedChanged = viewModel::onWhatsIncludedChanged,
            onScheduledDateChanged = viewModel::onScheduledDateChanged,
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
            onCreateTour = {}, // Not used
            buttonText = "Save Changes",
            onButtonClick = viewModel::onUpdateTour
        )
    }
}
