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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import com.tourly.app.home.domain.model.Tour

@Composable
fun DashboardContent(
    uiState: UserUiState,
    onCancelBooking: (Long) -> Unit,
    onDeleteTour: (Long) -> Unit,
    onEditTour: (Long) -> Unit,
    onCreateTour: () -> Unit,
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
                    text = "Please log in to see your dashboard",
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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var tourToDelete by remember { mutableStateOf<Tour?>(null) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }

    if (tourToDelete != null) {
        AlertDialog(
            onDismissRequest = { tourToDelete = null },
            title = { Text(text = "Delete Tour", fontFamily = OutfitFamily, fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete '${tourToDelete?.title}'? This action cannot be undone.", fontFamily = OutfitFamily) },
            confirmButton = {
                TextButton(
                    onClick = {
                        tourToDelete?.id?.let { onDeleteTour(it) }
                        tourToDelete = null
                    }
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { tourToDelete = null }) {
                    Text(text = "Cancel", fontFamily = OutfitFamily)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        if (uiState.user.role == UserRole.TRAVELER) {
            BookedToursSection(
                bookings = uiState.bookings.filter { it.status == "CONFIRMED" },
                onCancelBooking = { bookingId ->
                    bookingToCancel = uiState.bookings.find { it.id == bookingId }
                }
            )
        } else {
            // Guide Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Tours",
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
                    Text(text = "Create Tour", fontFamily = OutfitFamily)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.tours.isEmpty()) {
                Text(
                    text = "You haven't created any tours yet.",
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
