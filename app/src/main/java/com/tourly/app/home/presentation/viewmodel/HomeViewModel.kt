package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.home.domain.usecase.GetAllToursUseCase
import com.tourly.app.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeEvent {
    data object SessionExpired : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllToursUseCase: GetAllToursUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadTours()
    }

    fun loadTours() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = getAllToursUseCase()
            
            _uiState.value = result.fold(
                onSuccess = { tours -> HomeUiState.Success(tours) },
                onFailure = { exception -> 
                    val message = exception.message ?: "Failed to load tours"
                    if (message.contains("403") || message.contains("401")) {
                        _events.send(HomeEvent.SessionExpired)
                    }
                    HomeUiState.Error(message)
                }
            )
        }
    }
}
