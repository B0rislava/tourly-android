package com.tourly.app.home.presentation.state

import com.tourly.app.home.domain.model.Tag
import com.tourly.app.home.domain.model.TourFilters
import java.time.LocalDate

data class FilterUiState(
    val availableTags: List<Tag> = emptyList(),
    val selectedTags: List<String> = emptyList(),
    val sortBy: TourFilters.SortField = TourFilters.SortField.CREATED_AT,
    val sortOrder: TourFilters.SortOrder = TourFilters.SortOrder.DESC,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val selectedDate: LocalDate? = null,
    val isExpanded: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = selectedTags.isNotEmpty() || minPrice != null || maxPrice != null || selectedDate != null
}
