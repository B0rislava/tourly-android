package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tourly.app.home.presentation.ui.components.BookingDialog
import com.tourly.app.home.presentation.ui.components.BottomPriceBar
import com.tourly.app.home.presentation.viewmodel.TourDetailsUiState
import com.tourly.app.home.presentation.viewmodel.TourDetailsViewModel

import com.tourly.app.login.domain.UserRole

@Composable
fun TourDetailsScreen(
    viewModel: TourDetailsViewModel,
    userRole: UserRole?,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBookingDialog by remember { mutableStateOf(false) }

    // Handle booking side effects
    if (uiState is TourDetailsUiState.Success) {
        val state = uiState as TourDetailsUiState.Success
        
        LaunchedEffect(state.isBookingSuccess) {
            if (state.isBookingSuccess) {
                showBookingDialog = false
                snackbarHostState.showSnackbar("Tour booked successfully!")
                viewModel.resetBookingState()
            }
        }
        
        LaunchedEffect(state.bookingError) {
            state.bookingError?.let { error ->
                snackbarHostState.showSnackbar(error)
                viewModel.resetBookingState()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState is TourDetailsUiState.Success && userRole == UserRole.TRAVELER) {
                val tour = (uiState as TourDetailsUiState.Success).tour
                // Only show book button if spots available
                val canBook = tour.availableSpots > 0
                
                BottomPriceBar(
                    price = tour.pricePerPerson,
                    buttonText = if (canBook) "Book Now" else "Fully Booked",
                    isButtonEnabled = canBook,
                    onButtonClick = { showBookingDialog = true }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TourDetailsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TourDetailsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TourDetailsUiState.Success -> {
                    TourDetailsContent(
                        tour = state.tour,
                        onBackClick = onBackClick
                    )
                    
                    if (showBookingDialog) {
                        BookingDialog(
                            tourId = state.tour.id,
                            pricePerPerson = state.tour.pricePerPerson,
                            maxParticipants = state.tour.availableSpots,
                            isBooking = state.isBooking,
                            onConfirm = { tourId, participants ->
                                viewModel.bookTour(tourId, participants)
                            },
                            onDismiss = { showBookingDialog = false }
                        )
                    }
                }
            }
        }
    }
}

