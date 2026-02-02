package com.tourly.app.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.usecase.CancelBookingUseCase
import com.tourly.app.core.domain.usecase.CompleteBookingUseCase
import com.tourly.app.core.domain.usecase.GetMyBookingsUseCase
import com.tourly.app.core.domain.usecase.GetGuideBookingsUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.usecase.DeleteTourUseCase
import com.tourly.app.create_tour.domain.usecase.GetMyToursUseCase
import com.tourly.app.dashboard.presentation.state.DashboardUiState
import com.tourly.app.home.domain.usecase.GetSavedToursUseCase
import com.tourly.app.login.domain.UserRole
import com.tourly.app.reviews.domain.usecase.CreateReviewUseCase
import com.tourly.app.reviews.domain.usecase.GetMyReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getMyBookingsUseCase: GetMyBookingsUseCase,
    private val cancelBookingUseCase: CancelBookingUseCase,
    private val getMyToursUseCase: GetMyToursUseCase,
    private val deleteTourUseCase: DeleteTourUseCase,
    private val getSavedToursUseCase: GetSavedToursUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val completeBookingUseCase: CompleteBookingUseCase,
    private val getGuideBookingsUseCase: GetGuideBookingsUseCase,
    private val getMyReviewsUseCase: GetMyReviewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = Channel<String>()
    val events = _events.receiveAsFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            
            when (val userResult = getUserProfileUseCase()) {
                is Result.Success -> {
                    val user = userResult.data
                    if (user.role == UserRole.TRAVELER) {
                        fetchTravelerData(user)
                    } else {
                        fetchGuideData(user)
                    }
                }
                is Result.Error -> {
                    _uiState.value = DashboardUiState.Error(userResult.message)
                }
            }
        }
    }

    private suspend fun fetchTravelerData(user: User) {
        val bookingsResult = getMyBookingsUseCase()
        val savedToursResult = getSavedToursUseCase()

        if (bookingsResult is Result.Success && savedToursResult is Result.Success) {
            _uiState.value = DashboardUiState.Success(
                role = user.role,
                userName = user.firstName,
                bookings = bookingsResult.data,
                savedTours = savedToursResult.data
            )
        } else {
            val errorMsg = (bookingsResult as? Result.Error)?.message 
                ?: (savedToursResult as? Result.Error)?.message 
                ?: "Failed to load dashboard data"
            _uiState.value = DashboardUiState.Error(errorMsg)
        }
    }

    private suspend fun fetchGuideData(user: User) {
        val toursResult = getMyToursUseCase()
        val bookingsResult = getGuideBookingsUseCase()
        val reviewsResult = getMyReviewsUseCase()

        if (toursResult is Result.Success && bookingsResult is Result.Success && reviewsResult is Result.Success) {
            _uiState.value = DashboardUiState.Success(
                role = user.role,
                userName = user.firstName,
                guideRating = user.rating,
                reviewsCount = user.reviewsCount,
                tours = toursResult.data,
                bookings = bookingsResult.data,
                reviews = reviewsResult.data
            )
        } else {
            val errorMsg = (toursResult as? Result.Error)?.message 
                ?: (bookingsResult as? Result.Error)?.message 
                ?: (reviewsResult as? Result.Error)?.message 
                ?: "Failed to load dashboard data"
            _uiState.value = DashboardUiState.Error(errorMsg)
        }
    }

    fun cancelBooking(id: Long) {
        viewModelScope.launch {
            when (val result = cancelBookingUseCase(id)) {
                is Result.Success -> {
                    _events.send("Booking cancelled successfully")
                    loadDashboardData()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }

    fun deleteTour(id: Long) {
        viewModelScope.launch {
            when (val result = deleteTourUseCase(id)) {
                is Result.Success -> {
                    _events.send("Tour deleted successfully")
                    loadDashboardData()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }

    fun rateTour(bookingId: Long, tourRating: Int, guideRating: Int, comment: String) {
        viewModelScope.launch {
            when (val result = createReviewUseCase(bookingId, tourRating, guideRating, comment)) {
                is Result.Success -> {
                    _events.send("Thank you for your feedback!")
                    loadDashboardData()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }

    fun completeBooking(id: Long) {
        viewModelScope.launch {
            when (val result = completeBookingUseCase(id)) {
                is Result.Success -> {
                    _events.send("Booking completed successfully")
                    loadDashboardData()
                }
                is Result.Error -> {
                    _events.send(result.message)
                }
            }
        }
    }
}
