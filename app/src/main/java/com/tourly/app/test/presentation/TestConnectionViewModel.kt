package com.tourly.app.test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.network.api.TestApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TestConnectionState {
    data object Idle : TestConnectionState()
    data object Loading : TestConnectionState()
    data class Success(val message: String) : TestConnectionState()
    data class Error(val message: String) : TestConnectionState()
}

@HiltViewModel
class TestConnectionViewModel @Inject constructor(
    private val testApiService: TestApiService
) : ViewModel() {

    private val _state = MutableStateFlow<TestConnectionState>(TestConnectionState.Idle)
    val state: StateFlow<TestConnectionState> = _state.asStateFlow()

    fun testConnection() {
        viewModelScope.launch {
            _state.value = TestConnectionState.Loading
            
            testApiService.testPublicEndpoint()
                .onSuccess { response ->
                    _state.value = TestConnectionState.Success(response.message)
                }
                .onFailure { error ->
                    _state.value = TestConnectionState.Error(
                        error.message ?: "Unknown error occurred"
                    )
                }
        }
    }

    fun resetState() {
        _state.value = TestConnectionState.Idle
    }
}
