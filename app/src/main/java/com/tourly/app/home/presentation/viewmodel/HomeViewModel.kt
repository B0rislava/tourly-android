package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.network.Result
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tourly.app.home.domain.model.Tour
import com.tourly.app.home.domain.model.Tag
import com.tourly.app.home.domain.model.TourFilters
import com.tourly.app.home.domain.usecase.GetAllTagsUseCase
import com.tourly.app.home.domain.usecase.GetAllToursUseCase
import com.tourly.app.home.presentation.state.HomeUiState
import com.tourly.app.notifications.domain.usecase.GetUnreadNotificationsCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

sealed interface HomeEvent {
    data object SessionExpired : HomeEvent
    data class ShowSnackbar(val message: String) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllToursUseCase: GetAllToursUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val userRepository: UserRepository,
    private val clock: Clock,
    private val getUnreadNotificationsCountUseCase: GetUnreadNotificationsCountUseCase,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    private val _allTours = MutableStateFlow<List<Tour>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Available tags from backend
    private val _availableTags = MutableStateFlow<List<Tag>>(emptyList())
    val availableTags = _availableTags.asStateFlow()

    // Current filter state
    private val _filters = MutableStateFlow(TourFilters())
    val filters = _filters.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _greeting = MutableStateFlow("")
    val greeting = _greeting.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    private val _addressPredictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val addressPredictions = _addressPredictions.asStateFlow()



    val uiState: StateFlow<HomeUiState> = combine(
        _isLoading,
        _error,
        _allTours,
        _searchQuery
    ) { isLoading, error, tours, query ->
        when {
            isLoading -> HomeUiState.Loading
            error != null -> HomeUiState.Error(error)
            else -> {
                // Only filter by search query client-side
                val filteredTours = if (query.isBlank()) {
                    tours
                } else {
                    tours.filter { tour ->
                        tour.title.contains(query, ignoreCase = true) || 
                        tour.location.contains(query, ignoreCase = true)
                    }
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
        observeTokenChanges()
        updateGreeting()
        loadTags()
        fetchUnreadCount()
    }
    
    private fun observeTokenChanges() {
        viewModelScope.launch {
            userRepository.getTokenFlow().collectLatest { hasToken ->
                if (hasToken) {
                    loadUserProfile()
                } else {
                    _userProfile.value = null
                    _allTours.value = emptyList()
                    _filters.value = TourFilters()
                    _searchQuery.value = ""
                    _isLoading.value = false
                    _isRefreshing.value = false
                    _error.value = null
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun loadTags() {
        viewModelScope.launch {
            when (val result = getAllTagsUseCase()) {
                is Result.Success -> {
                    _availableTags.value = result.data
                }
                is Result.Error -> {
                    _events.send(HomeEvent.ShowSnackbar("Failed to load filters"))
                }
            }
        }
    }

    fun updateFilters(update: (TourFilters) -> TourFilters) {
        _filters.value = update(_filters.value)
        _isLoading.value = true
        loadTours()
    }

    fun updateSort(field: TourFilters.SortField, order: TourFilters.SortOrder) {
        updateFilters { it.copy(sortBy = field, sortOrder = order) }
    }

    fun updatePriceRange(min: Double, max: Double) {
        updateFilters { it.copy(minPrice = min, maxPrice = max) }
    }
    
    fun updateDate(date: LocalDate?) {
        updateFilters { it.copy(scheduledAfter = date, scheduledBefore = date) } 
    }

    fun updateLocation(location: String?) {
        updateFilters { it.copy(location = location) }
        _addressPredictions.value = emptyList()
    }

    fun fetchLocationPredictions(query: String) {
        if (query.length < 3) {
            _addressPredictions.value = emptyList()
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setTypesFilter(listOf(PlaceTypes.CITIES))
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _addressPredictions.value = response.autocompletePredictions
            }
            .addOnFailureListener {
                _addressPredictions.value = emptyList()
            }
    }

    fun toggleTag(tagName: String) {
        updateFilters { filters ->
            val newTags = if (tagName in filters.tags) {
                filters.tags - tagName
            } else {
                filters.tags + tagName
            }
            filters.copy(tags = newTags)
        }
    }

    fun clearFilters() {
        updateFilters { it.reset() }
    }

    fun refreshData() {
        loadUserProfile()
        updateGreeting()
        fetchUnreadCount()
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
                    loadTours()
                }
                is Result.Error -> {
                    _isLoading.value = false
                    _isRefreshing.value = false
                    _events.send(HomeEvent.ShowSnackbar(result.message))
                }
            }
            fetchUnreadCount()
        }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            when (val result = getUnreadNotificationsCountUseCase()) {
                is Result.Success -> _unreadCount.value = result.data
                else -> { /* badge error */ }
            }
        }
    }

    fun loadTours() {
        viewModelScope.launch {
            _error.value = null
            
            when (val result = getAllToursUseCase(_filters.value)) {
                is Result.Success -> {
                    _allTours.value = result.data
                    _isLoading.value = false
                    _isRefreshing.value = false
                }
                is Result.Error -> {
                    if (result.code == "FORBIDDEN" || result.code == "UNAUTHORIZED") {
                        _events.send(HomeEvent.SessionExpired)
                    }
                    _error.value = result.message
                    _isLoading.value = false
                }
            }
            fetchUnreadCount()
        }
    }
}
