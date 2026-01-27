package com.tourly.app.dashboard.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tourly.app.core.presentation.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    userViewModel: UserViewModel,
    onEditTour: (Long) -> Unit,
    onCreateTour: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by userViewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onCancelBooking = userViewModel::cancelBooking,
        onDeleteTour = userViewModel::deleteTour,
        onEditTour = onEditTour,
        onCreateTour = onCreateTour,
        modifier = modifier
    )
}
