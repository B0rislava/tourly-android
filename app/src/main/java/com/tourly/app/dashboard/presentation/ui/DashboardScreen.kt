package com.tourly.app.dashboard.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.dashboard.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onEditTour: (Long) -> Unit,
    onCreateTour: () -> Unit,
    onTourClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    DashboardContent(
        uiState = uiState,
        onCancelBooking = viewModel::cancelBooking,
        onDeleteTour = viewModel::deleteTour,
        onEditTour = onEditTour,
        onCreateTour = onCreateTour,
        onTourClick = onTourClick,
        onRateTour = viewModel::rateTour,
        onCompleteBooking = viewModel::completeBooking,
        modifier = modifier
    )
}
