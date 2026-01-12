package com.tourly.app.home.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tourly.app.home.presentation.viewmodel.GuideHomeViewModel

@Composable
fun GuideHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: GuideHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Refresh tours when the screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadTours()
    }

    GuideHomeContent(
        modifier = modifier,
        state = state
    )
}
