package com.tourly.app.dashboard.presentation.state

import com.tourly.app.core.domain.model.Booking
import com.tourly.app.core.domain.model.Review
import com.tourly.app.core.domain.model.Tour
import com.tourly.app.login.domain.UserRole

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val role: UserRole,
        val userName: String = "",
        val guideRating: Double = 0.0,
        val reviewsCount: Int = 0,
        val bookings: List<Booking> = emptyList(),
        val tours: List<Tour> = emptyList(),
        val savedTours: List<Tour> = emptyList(),
        val reviews: List<Review> = emptyList()
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}
