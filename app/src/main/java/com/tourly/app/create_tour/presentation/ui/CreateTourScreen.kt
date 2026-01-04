package com.tourly.app.create_tour.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.create_tour.presentation.viewmodel.CreateTourViewModel

@Composable
fun CreateTourScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateTourViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreateTourContent(
        modifier = modifier,
        state = uiState,
        onTourTypeChanged = viewModel::onTourTypeChanged,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onLocationChanged = viewModel::onLocationChanged,
        onDurationChanged = viewModel::onDurationChanged,
        onMaxGroupSizeChanged = viewModel::onMaxGroupSizeChanged,
        onPriceChanged = viewModel::onPriceChanged,
        onWhatsIncludedChanged = viewModel::onWhatsIncludedChanged,
        onAddDay = viewModel::onAddDay,
        onRemoveDay = viewModel::onRemoveDay,
        onDayDescriptionChanged = viewModel::onDayDescriptionChanged,
        onCreateTour = { /* TODO: Call ViewModel create */ }
    )
}








