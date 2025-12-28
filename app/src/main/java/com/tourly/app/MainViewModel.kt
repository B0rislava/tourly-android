package com.tourly.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.storage.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                android.util.Log.d("MainViewModel", "checkSession: token=$token")
                val isLoggedIn = token != null
                android.util.Log.d("MainViewModel", "checkSession: Updating isUserLoggedIn to $isLoggedIn")
                _uiState.value = MainActivityUiState.Success(isUserLoggedIn = isLoggedIn)
            }
        }
    }
}
