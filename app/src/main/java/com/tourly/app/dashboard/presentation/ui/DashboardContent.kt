package com.tourly.app.dashboard.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.ui.components.BookedToursSection
import com.tourly.app.profile.presentation.ui.components.GuideTourCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import com.tourly.app.bookings.presentation.ui.RateExperienceDialog
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.core.presentation.ui.components.TourlyAlertDialog

@Composable
fun DashboardContent(
    uiState: UserUiState,
    onCancelBooking: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
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
            is UserUiState.Loading -> {
                CircularProgressIndicator()
            }
            is UserUiState.Success -> {
                DashboardSuccessContent(
                    uiState = uiState,
                    onCancelBooking = onCancelBooking,
                    onDeleteTour = onDeleteTour,
                    onEditTour = onEditTour,
                    onCreateTour = onCreateTour,
                    onRateTour = onRateTour,
                    onCompleteBooking = onCompleteBooking,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is UserUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = OutfitFamily
                )
            }
            else -> {
                Text(
                    text = stringResource(id = R.string.login_to_see_dashboard),
                    fontFamily = OutfitFamily
                )
            }
        }
    }
}

@Composable
private fun DashboardSuccessContent(
    uiState: UserUiState.Success,
    onCancelBooking: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onCreateTour: () -> Unit,
    onRateTour: (Long, Int, Int, String) -> Unit,
    onCompleteBooking: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var tourToDelete by remember { mutableStateOf<Tour?>(null) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var showRateDialog by remember { mutableStateOf<Booking?>(null) }

    if (tourToDelete != null) {
        TourlyAlertDialog(
            onDismissRequest = { tourToDelete = null },
            onConfirm = {
                tourToDelete?.id?.let { onDeleteTour(it) }
            },
            title = stringResource(id = R.string.delete_tour),
            text = stringResource(id = R.string.delete_tour_confirmation, tourToDelete?.title ?: ""),
            confirmButtonText = stringResource(id = R.string.delete),
            isDestructive = true
        )
    }

    if (bookingToCancel != null) {
        TourlyAlertDialog(
            onDismissRequest = { bookingToCancel = null },
            onConfirm = {
                bookingToCancel?.id?.let { onCancelBooking(it) }
            },
            title = stringResource(id = R.string.cancel_booking),
            text = stringResource(id = R.string.cancel_booking_confirmation, bookingToCancel?.tourTitle ?: ""),
            confirmButtonText = stringResource(id = R.string.yes_cancel),
            dismissButtonText = stringResource(id = R.string.no_keep),
            isDestructive = true
        )
    }

    if (showRateDialog != null) {
        RateExperienceDialog(
            onDismissRequest = { showRateDialog = null },
            onConfirm = { tourRating, guideRating, comment ->
                showRateDialog?.id?.let { bookingId ->
                    onRateTour(bookingId, tourRating, guideRating, comment)
                }
                showRateDialog = null
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        if (uiState.user.role == UserRole.TRAVELER) {
            val upcomingBookings = uiState.bookings
                .filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
                .sortedBy { it.tourScheduledDate }
            
            val pastBookings = uiState.bookings
                .filter { it.status == "COMPLETED" }
                .sortedByDescending { it.tourScheduledDate }

            BookedToursSection(
                title = stringResource(id = R.string.upcoming_tours),
                bookings = upcomingBookings,
                onCancelBooking = { bookingId ->
                    bookingToCancel = uiState.bookings.find { it.id == bookingId }
                },
                onCompleteBooking = onCompleteBooking,
                showRateButton = false
            )

            
            Spacer(modifier = Modifier.height(24.dp))
            
            BookedToursSection(
                title = stringResource(id = R.string.past_tours),
                bookings = pastBookings,
                onCancelBooking = {}, 
                onRateClick = { bookingId ->
                    showRateDialog = uiState.bookings.find { it.id == bookingId }
                },
                onCompleteBooking = onCompleteBooking,
                showRateButton = true
            )


        } else {
            // Guide Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.my_tours),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )

                
                TextButton(onClick = onCreateTour) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.create_tour), fontFamily = OutfitFamily)
                }

            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.tours.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_tours_created),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {

                uiState.tours.forEach { tour ->
                    GuideTourCard(
                        tour = tour,
                        onEdit = { onEditTour(tour.id) },
                        onDelete = { tourToDelete = tour }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

