package com.tourly.app.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.domain.repository.LanguageRepository
import com.tourly.app.core.domain.model.AppLanguage
import com.tourly.app.core.network.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.tourly.app.core.domain.usecase.DeleteAccountUseCase
import com.tourly.app.core.domain.usecase.GetUserProfileUseCase
import com.tourly.app.core.domain.usecase.LogoutUseCase
import com.tourly.app.core.domain.usecase.ObserveAuthStateUseCase
import com.tourly.app.core.domain.usecase.ObserveUserProfileUseCase
import javax.inject.Inject

import com.tourly.app.core.domain.model.ThemeMode

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val observeUserProfileUseCase: ObserveUserProfileUseCase
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.themeMode.collect {
                _themeMode.value = it
            }
        }

        viewModelScope.launch {
            languageRepository.currentLanguage.collect {
                _currentLanguage.value = it
            }
        }
        
        // Observe global user flow via UseCase to refresh user data automatically everywhere
        viewModelScope.launch {
            observeUserProfileUseCase().collect { user ->
                if (user != null) {
                    _user.value = user
                } else {
                    // Check if we are logged in - if so, try to fetch
                    observeAuthStateUseCase()
                        .distinctUntilChanged()
                        .collect { isLoggedIn ->
                            if (isLoggedIn && _user.value == null) {
                                refreshUserProfile()
                            } else if (!isLoggedIn) {
                                _user.value = null
                            }
                        }
                }
            }
        }
    }

    fun refreshUserProfile() {
        viewModelScope.launch {
            when (val result = getUserProfileUseCase()) {
                is Result.Success -> _user.value = result.data
                is Result.Error -> {
                    // TODO handle error
                }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeRepository.setThemeMode(mode)
        }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            languageRepository.setLanguage(language)
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            onLogoutComplete()
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            when (deleteAccountUseCase()) {
                is Result.Success -> {
                    onSuccess()
                }
                is Result.Error -> {
                    // TODO: Handle error
                }
            }
        }
    }
}
