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
import com.tourly.app.home.presentation.viewmodel.TourDetailsEvent

import com.tourly.app.login.domain.UserRole

import androidx.compose.ui.res.stringResource
import com.tourly.app.R

@Composable
fun TourDetailsScreen(
    viewModel: TourDetailsViewModel,
    userRole: UserRole?,
    onBackClick: () -> Unit,
    onGuideClick: (Long) -> Unit = {},
    onBookingSuccess: () -> Unit = {},
    onEditTour: (Long) -> Unit = {}
) {
    val uiState: TourDetailsUiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBookingDialog by remember { mutableStateOf(false) }

    // Handle one-time UI events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TourDetailsEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is TourDetailsEvent.BookingSuccess -> {
                    showBookingDialog = false
                    snackbarHostState.showSnackbar("Tour booked successfully!")
                    onBookingSuccess()
                }
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
                    buttonText = if (canBook) stringResource(id = R.string.book_now) else stringResource(id = R.string.fully_booked),
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
                        reviews = state.reviews,
                        userRole = userRole,
                        onEditTour = onEditTour,
                        onBackClick = onBackClick,
                        onGuideClick = onGuideClick,
                        onToggleSave = { viewModel.toggleSaveTour(state.tour.id) }
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

