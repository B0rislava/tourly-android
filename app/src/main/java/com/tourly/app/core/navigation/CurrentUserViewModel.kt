package com.tourly.app.core.navigation

import androidx.lifecycle.ViewModel
import com.tourly.app.core.domain.usecase.ObserveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CurrentUserViewModel @Inject constructor(
    observeUserProfileUseCase: ObserveUserProfileUseCase
) : ViewModel() {
    
    val currentUserId: Flow<Long?> = observeUserProfileUseCase()
        .map { user -> user?.id }
}
