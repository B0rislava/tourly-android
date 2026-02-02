package com.tourly.app.dashboard.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.dashboard.presentation.state.DashboardUiState

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onCancelBooking: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onTourClick: (Long) -> Unit,
    onCreateTour: () -> Unit,
    onRateTour: (Long, Int, Int, String) -> Unit, // bookingId, tourRating, guideRating, comment
    onCompleteBooking: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is DashboardUiState.Loading -> {
                CircularProgressIndicator()
            }
            is DashboardUiState.Success -> {
                DashboardSuccessContent(
                    uiState = uiState,
                    onCancelBooking = onCancelBooking,
                    onDeleteTour = onDeleteTour,
                    onEditTour = onEditTour,
                    onTourClick = onTourClick,
                    onCreateTour = onCreateTour,
                    onRateTour = onRateTour,
                    onCompleteBooking = onCompleteBooking,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is DashboardUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = OutfitFamily
                )
            }
        }
    }
}

