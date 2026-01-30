package com.tourly.app.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.TourFilters
import com.tourly.app.home.presentation.state.FilterUiState
import java.time.LocalDate

@Composable
fun HomeFilterSection(
    uiState: FilterUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTagToggle: (String) -> Unit,
    onSortSelected: (TourFilters.SortField, TourFilters.SortOrder) -> Unit,
    onPriceRangeChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onDateSelected: (LocalDate?) -> Unit,
    onLocationSearch: (String) -> Unit,
    onLocationSelected: (String?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Row 1: Search Bar (Weight 1) + Date Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            Alignment.CenterVertically
        ) {
            HomeSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.weight(1f)
            )
            
            DateFilterButton(
                selectedDate = uiState.selectedDate,
                onDateSelected = onDateSelected
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        // Row 2: Price + Sort + Location
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val priceRange = (uiState.minPrice?.toFloat() ?: 0f)..(uiState.maxPrice?.toFloat() ?: 500f)
            
            PriceFilterPopup(
                priceRange = priceRange,
                onPriceRangeChanged = onPriceRangeChanged,
                modifier = Modifier.weight(1f)
            )

            SortMenu(
                currentSortField = uiState.sortBy,
                currentSortOrder = uiState.sortOrder,
                onSortSelected = onSortSelected,
                modifier = Modifier.weight(1f)
            )

            LocationFilterPopup(
                selectedLocation = uiState.selectedLocation,
                predictions = uiState.addressPredictions,
                onSearchTextChange = onLocationSearch,
                onLocationSelected = onLocationSelected,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.size(12.dp))

        // Row 3: Categories (Tags)
        CategoryRow(
            tags = uiState.availableTags,
            selectedTags = uiState.selectedTags,
            onTagToggle = onTagToggle
        )
        
        if (uiState.hasActiveFilters) {
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(
                onClick = onClearFilters,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    // TODO: Extract text
                    text = "Clear all filters",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


