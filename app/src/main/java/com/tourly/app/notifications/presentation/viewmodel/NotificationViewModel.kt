package com.tourly.app.notifications.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.core.network.Result
import com.tourly.app.notifications.domain.model.Notification
import com.tourly.app.notifications.domain.usecase.GetNotificationsUseCase
import com.tourly.app.notifications.domain.usecase.GetUnreadNotificationsCountUseCase
import com.tourly.app.notifications.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.tourly.app.notifications.domain.usecase.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadNotificationsCountUseCase: GetUnreadNotificationsCountUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val notificationsResult = getNotificationsUseCase()
            val countResult = getUnreadNotificationsCountUseCase()

            if (notificationsResult is Result.Success && countResult is Result.Success) {
                _uiState.update { 
                    it.copy(
                        notifications = notificationsResult.data,
                        unreadCount = countResult.data,
                        isLoading = false
                    )
                }
            } else {
                val errorMessage = (notificationsResult as? Result.Error)?.message 
                    ?: (countResult as? Result.Error)?.message 
                    ?: "Failed to load notifications"
                _uiState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            when (val result = markNotificationAsReadUseCase(id)) {
                is Result.Success -> {
                    // Optimistic update or reload
                    _uiState.update { state ->
                        val updatedList = state.notifications.map { 
                            if (it.id == id) it.copy(isRead = true) else it
                        }
                        state.copy(
                            notifications = updatedList,
                            unreadCount = (state.unreadCount - 1).coerceAtLeast(0)
                        )
                    }
                }
                is Result.Error -> {
                    // Show error
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            when (val result = markAllNotificationsAsReadUseCase()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        val updatedList = state.notifications.map { it.copy(isRead = true) }
                        state.copy(notifications = updatedList, unreadCount = 0)
                    }
                }
                is Result.Error -> {
                    // Show error
                }
            }
        }
    }
}

// Extension to update flow state easily
private inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
    val prevValue = this.value
    val nextValue = function(prevValue)
    this.value = nextValue
}
