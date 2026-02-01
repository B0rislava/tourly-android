package com.tourly.app.core.domain.model

import java.time.LocalDate

data class TourFilters(
    val tags: List<String> = emptyList(),
    val location: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double? = null,
    val scheduledAfter: LocalDate? = null,
    val scheduledBefore: LocalDate? = null,
    val sortBy: SortField = SortField.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC
) {
    enum class SortField {
        CREATED_AT, PRICE, RATING, DURATION
    }
    
    enum class SortOrder {
        ASC, DESC
    }

    // Reset all filters to default state
    fun reset(): TourFilters = TourFilters()
}
