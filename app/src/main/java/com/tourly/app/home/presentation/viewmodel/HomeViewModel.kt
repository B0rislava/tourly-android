package com.tourly.app.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.tourly.app.core.navigation.Route
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
class HomeViewModel @AssistedInject constructor(
    @Assisted val route: Route.Home
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(route: Route.Home): HomeViewModel
    }
}
