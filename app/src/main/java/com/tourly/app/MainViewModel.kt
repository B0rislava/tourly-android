package com.tourly.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.domain.usecase.LogoutUseCase
import com.tourly.app.core.domain.repository.LanguageRepository
import com.tourly.app.core.domain.usecase.ObserveAuthStateUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.network.Result
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
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    themeRepository: ThemeRepository,
    languageRepository: LanguageRepository
) : ViewModel() {


    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    
    val uiState: StateFlow<MainActivityUiState> = combine(
        _sessionState,
        themeRepository.themeMode,
        languageRepository.currentLanguage
    ) { session, themeMode, appLanguage ->

        when (session) {
            is SessionState.Loading -> MainActivityUiState.Loading
            is SessionState.Success -> MainActivityUiState.Success(
                isUserLoggedIn = session.isLoggedIn,
                userRole = session.userRole,
                themeMode = themeMode,
                appLanguage = appLanguage
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
            observeAuthStateUseCase().collect { isLoggedIn ->
                Log.d("MainViewModel", "checkSession: isLoggedIn=$isLoggedIn")
                var userRole: UserRole? = null

                if (isLoggedIn) {
                    when (val result = getUserProfileUseCase()) {
                         is Result.Success<User> -> {
                             userRole = result.data.role
                         }
                         is Result.Error -> {
                             Log.e("MainViewModel", "Failed to fetch user profile: ${result.message} (Code: ${result.code})")
                             if (result.code == "UNAUTHORIZED") {
                                 // Only clear tokens if specifically unauthorized
                                 logoutUseCase()
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
            logoutUseCase()
            onLogoutComplete()
        }
    }
}
