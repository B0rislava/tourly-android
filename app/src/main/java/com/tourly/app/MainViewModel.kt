package com.tourly.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.Result
import com.tourly.app.core.storage.TokenManager
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    themeRepository: ThemeRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    
    val uiState: StateFlow<MainActivityUiState> = combine(
        _sessionState,
        themeRepository.isDarkTheme
    ) { session, isDark ->
        when (session) {
            is SessionState.Loading -> MainActivityUiState.Loading
            is SessionState.Success -> MainActivityUiState.Success(
                isUserLoggedIn = session.isLoggedIn,
                userRole = session.userRole,
                isDarkTheme = isDark
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActivityUiState.Loading
    )

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            tokenManager.getTokenFlow().collect { token ->
                Log.d("MainViewModel", "checkSession: token=$token")
                val isLoggedIn = token != null
                var userRole: UserRole? = null

                if (isLoggedIn) {
                    when (val result = userRepository.getUserProfile()) {
                         is Result.Success<User> -> {
                             userRole = result.data.role
                         }
                         is Result.Error -> {
                             Log.e("MainViewModel", "Failed to fetch user profile: ${result.message} (Code: ${result.code})")
                             if (result.code == "UNAUTHORIZED") {
                                 // Only clear tokens if specifically unauthorized
                                 tokenManager.clearToken()
                                 tokenManager.clearRefreshToken()
                             }
                         }
                    }
                }
                _sessionState.value = SessionState.Success(isLoggedIn, userRole)
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLogoutComplete()
        }
    }
}
