package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.core.domain.model.TourFilters
import com.tourly.app.home.presentation.state.FilterUiState
import com.tourly.app.home.presentation.state.HomeUiState
import com.tourly.app.home.presentation.ui.components.EmptyState
import com.tourly.app.home.presentation.ui.components.ErrorState
import com.tourly.app.home.presentation.ui.components.HomeFilterSection
import com.tourly.app.home.presentation.ui.components.HomeHeaderSection
import com.tourly.app.core.presentation.ui.components.TourItemCard
import com.tourly.app.login.domain.UserRole
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterUiState: FilterUiState,
    onTagToggle: (String) -> Unit,
    onSortSelected: (TourFilters.SortField, TourFilters.SortOrder) -> Unit,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onDateSelected: (LocalDate?) -> Unit,
    onLocationSearch: (String) -> Unit,
    onLocationSelected: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onRefresh: () -> Unit,
    greeting: Int,
    userName: String,
    isRefreshing: Boolean,
    onTourClick: (Long) -> Unit,
    onNotifyClick: () -> Unit,
    unreadCount: Int,
    userRole: UserRole? = null,
) {

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        if (uiState is HomeUiState.Error) {
            ErrorState(
                message = uiState.message,
                errorCode = uiState.code,
                onRetry = onRefresh,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Header Section (Always visible)
                item {
                    HomeHeaderSection(
                        greeting = stringResource(id = greeting),
                        userName = userName,
                        onNotifyClick = onNotifyClick,
                        unreadCount = unreadCount,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp)
                    )
                }

                // 2. Filters (Includes Search Bar now)
                item {
                    HomeFilterSection(
                        uiState = filterUiState,
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onTagToggle = onTagToggle,
                        onSortSelected = onSortSelected,
                        onPriceRangeChanged = onPriceRangeChanged,
                        onDateSelected = onDateSelected,
                        onLocationSearch = onLocationSearch,
                        onLocationSelected = onLocationSelected,
                        onClearFilters = onClearFilters,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 3. Content based on UI State
                when (uiState) {
                    is HomeUiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is HomeUiState.Success -> {
                        if (uiState.tours.isEmpty()) {
                            item {
                                when {
                                    searchQuery.isNotEmpty() -> {
                                        EmptyState(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 32.dp),
                                            title = stringResource(id = R.string.no_results_found),
                                            description = stringResource(
                                                id = R.string.no_results_description,
                                                searchQuery
                                            )
                                        )
                                    }

                                    filterUiState.hasActiveFilters -> {
                                        EmptyState(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 32.dp),
                                            title = stringResource(id = R.string.no_tours_found),
                                            description = stringResource(id = R.string.no_tours_description)
                                        )
                                    }

                                    else -> {
                                        EmptyState(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 32.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Header with Count
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.available_tours),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontFamily = OutfitFamily
                                        )

                                        // Count Badge
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = pluralStringResource(
                                                    id = R.plurals.tour_count,
                                                    count = uiState.tours.size,
                                                    uiState.tours.size
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                )
                                            )

                                        }
                                    }
                                }
                            }

                            items(uiState.tours, key = { it.id }) { tour ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    TourItemCard(
                                        tour = tour,
                                        userRole = userRole,
                                        onClick = { onTourClick(tour.id) }
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}