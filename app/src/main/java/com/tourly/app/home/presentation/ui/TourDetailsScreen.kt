package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tourly.app.home.presentation.ui.components.BottomPriceBar
import com.tourly.app.home.presentation.viewmodel.TourDetailsUiState
import com.tourly.app.home.presentation.viewmodel.TourDetailsViewModel

@Composable
fun TourDetailsScreen(
    viewModel: TourDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            if (uiState is TourDetailsUiState.Success) {
                val tour = (uiState as TourDetailsUiState.Success).tour
                BottomPriceBar(price = tour.pricePerPerson)
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
                }
            }
        }
    }
}







