package com.tourly.app.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.domain.repository.ThemeRepository
import com.tourly.app.core.domain.repository.UserRepository
import com.tourly.app.core.network.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.tourly.app.core.domain.model.ThemeMode

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.themeMode.collect {
                _themeMode.value = it
            }
        }
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            when (val result = userRepository.getUserProfile()) {
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

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.logout()
            onLogoutComplete()
        }
    }
}
