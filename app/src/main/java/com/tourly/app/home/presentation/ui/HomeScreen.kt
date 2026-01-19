package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.home.presentation.viewmodel.HomeEvent
import com.tourly.app.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onSessionExpired: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    val userProfile by viewModel.userProfile.collectAsState()
    val greeting by viewModel.greeting.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.SessionExpired -> onSessionExpired()
                is HomeEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            selectedCategory = selectedCategory,
            onCategoryChange = viewModel::onCategoryChange,
            onRefresh = viewModel::refreshData,
            greeting = greeting,
            userName = userProfile?.fullName ?: "Traveler", // Fallback name
            isDarkTheme = isDarkTheme,
            onThemeToggle = viewModel::toggleTheme,
            isRefreshing = isRefreshing,
            modifier = Modifier.padding(paddingValues)
        )
    }
}   