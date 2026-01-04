package com.tourly.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.login.domain.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainActivityUiState>(MainActivityUiState.Loading)
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                Log.d("MainViewModel", "checkSession: token=$token")
                val isLoggedIn = token != null
                var userRole: UserRole? = null

                if (isLoggedIn && token != null) {
                    val result = userRepository.getUserProfile(token)
                    result.onSuccess { userDto ->
                        userRole = userDto.role
                    }.onFailure {
                        // TODO: Handle error
                        Log.e("MainViewModel", "Failed to fetch user profile", it)
                    }
                }

                Log.d("MainViewModel", "checkSession: Updating isUserLoggedIn to $isLoggedIn, role=$userRole")
                _uiState.value = MainActivityUiState.Success(
                    isUserLoggedIn = isLoggedIn,
                    userRole = userRole
                )
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearToken()
            onLogoutComplete()
        }
    }
}
