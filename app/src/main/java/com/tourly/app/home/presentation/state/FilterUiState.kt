package com.tourly.app.home.presentation.state

/**
 * State for the Home Filter Section.
 * 
 * Future improvements to this state could include:
 * - dateRange: LongRange? = null (For selected date filter)
 * - selectedLocation: String? = null (For location filter)
 * - priceRange: ClosedFloatingPointRange<Float>? = null
 * - selectedRating: Int? = null
 */
data class FilterUiState(
    val selectedCategory: String = "All"
)
