package com.tourly.app.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tourly.app.home.presentation.state.FilterUiState

@Composable
fun HomeFilterSection(
    uiState: FilterUiState,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Top Row: Date and Location Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // TODO: Extract into strings.xml
            FilterButton(
                text = "Any date",
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1f)
            )
            FilterButton(
                text = "All Locations",
                icon = Icons.Default.LocationOn,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.size(16.dp))

        // Bottom Row: Categories
        CategoryRow(
            categories = listOf("All", "Adventure", "Culture", "Nature", "Food", "Relax"),
            selectedCategory = uiState.selectedCategory,
            onCategorySelected = onCategorySelected
        )
    }
}


