package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tourly.app.home.presentation.state.FilterUiState
import com.tourly.app.home.presentation.state.HomeUiState
import com.tourly.app.home.presentation.ui.components.EmptyState
import com.tourly.app.home.presentation.ui.components.ErrorState
import com.tourly.app.home.presentation.ui.components.HomeFilterSection
import com.tourly.app.home.presentation.ui.components.HomeHeaderSection
import com.tourly.app.home.presentation.ui.components.HomeSearchBar
import com.tourly.app.home.presentation.ui.components.TourItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    greeting: String,
    userName: String,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isRefreshing: Boolean,
    onTourClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = onRefresh,
                    modifier = Modifier.fillMaxSize()
                )
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Header Section (Greetings)
                    item {
                        HomeHeaderSection(
                            greeting = greeting,
                            userName = userName,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp)
                        )
                    }

                    // 2. Search Bar
                    item {
                        HomeSearchBar(
                            query = searchQuery,
                            onQueryChange = onSearchQueryChange,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // 3. Filters
                    item {
                        HomeFilterSection(
                            uiState = FilterUiState(selectedCategory = selectedCategory),
                            onCategorySelected = onCategoryChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // 4. Tour List
                    if (uiState.tours.isEmpty()) {
                        item {
                            if (searchQuery.isNotEmpty()) {
                                EmptyState(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp),
                                    // TODO: Strings.xml
                                    title = "No results found",
                                    description = "We couldn't find any tours matching \"$searchQuery\". Try adjusting your search."
                                )
                            } else {
                                EmptyState(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp)
                                )
                            }
                        }
                    } else {
                        items(uiState.tours, key = { it.id }) { tour ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                TourItemCard(
                                    tour = tour,
                                    onClick = { onTourClick(tour.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}