package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.Result
import com.tourly.app.create_tour.domain.repository.TourRepository
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.usecase.GetAllToursUseCase
import com.tourly.app.home.presentation.state.HomeUiState
import com.tourly.app.login.domain.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalTime
import javax.inject.Inject

sealed interface HomeEvent {
    data object SessionExpired : HomeEvent
    data class ShowSnackbar(val message: String) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllToursUseCase: GetAllToursUseCase,
    private val userRepository: UserRepository,
    private val clock: Clock,
    private val themeRepository: ThemeRepository,
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    private val _allTours = MutableStateFlow<List<Tour>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _greeting = MutableStateFlow("")
    val greeting = _greeting.asStateFlow()

    val isDarkTheme = themeRepository.isDarkTheme

    val uiState: StateFlow<HomeUiState> = combine(
        _isLoading,
        _error,
        _allTours,
        _searchQuery,
        _selectedCategory
    ) { isLoading, error, tours, query, category ->
        when {
            isLoading -> HomeUiState.Loading
            error != null -> HomeUiState.Error(error)
            else -> {
                val filteredTours = tours.filter { tour ->
                    val matchesQuery = tour.title.contains(query, ignoreCase = true) || 
                                       tour.location.contains(query, ignoreCase = true)
                    val matchesCategory = if (category == "All") true else {
                        // TODO: Add category field to Tour model. For now, mock strict matching.
                        true 
                    }
                    matchesQuery && matchesCategory
                }
                HomeUiState.Success(filteredTours)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadUserProfile()
        updateGreeting()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryChange(category: String) {
        _selectedCategory.value = category
    }

    fun refreshData() {
        loadUserProfile()
        updateGreeting()
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themeRepository.toggleTheme()
        }
    }

    private fun updateGreeting() {
        val currentHour = LocalTime.now(clock).hour
        _greeting.value = when (currentHour) {
            // TODO: Extract strings
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            if (_userProfile.value == null) {
                _isLoading.value = true
            } else {
                _isRefreshing.value = true
            }
            
            when (val result = userRepository.getUserProfile()) {
                is Result.Success -> {
                    _userProfile.value = result.data
                    loadTours() // Chain: now that we know the role, load tours
                }
                is Result.Error -> {
                    _isLoading.value = false
                    _isRefreshing.value = false
                    _events.send(HomeEvent.ShowSnackbar(result.message))
                }
            }
        }
    }

    fun loadTours() {
        viewModelScope.launch {
            _error.value = null
            
            // Check if user is a guide or traveler
            val currentUser = _userProfile.value
            val isGuide = currentUser?.role == UserRole.GUIDE
            
            if (isGuide) {
                // Guide: Fetch "my tours"
                val result = tourRepository.getMyTours()
                result.onSuccess { tours ->
                    _allTours.value = tours
                    _isLoading.value = false
                    _isRefreshing.value = false
                }.onFailure { exception ->
                    val message = exception.message ?: "Failed to load tours"
                    if (message.contains("403") || message.contains("401")) {
                        _events.send(HomeEvent.SessionExpired)
                    }
                    _error.value = message
                    _isLoading.value = false
                    _isRefreshing.value = false
                }
            } else {
                // Traveler: Fetch all tours
                val result = getAllToursUseCase()
                result.fold(
                    onSuccess = { tours -> 
                        _allTours.value = tours
                        _isLoading.value = false
                        _isRefreshing.value = false
                    },
                    onFailure = { exception -> 
                        val message = exception.message ?: "Failed to load tours"
                        if (message.contains("403") || message.contains("401")) {
                            _events.send(HomeEvent.SessionExpired)
                        }
                        _error.value = message
                        _isLoading.value = false
                        _isRefreshing.value = false
                    }
                )
            }
        }
    }
}
