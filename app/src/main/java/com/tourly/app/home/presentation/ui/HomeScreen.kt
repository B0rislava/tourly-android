package com.tourly.app.home.presentation.ui

import androidx.compose.runtime.Composable
import com.tourly.app.home.presentation.viewmodel.HomeViewModel
import com.tourly.app.core.ui.utils.WindowSizeState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.core.presentation.state.UserUiState
import com.tourly.app.core.presentation.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    userViewModel: UserViewModel = hiltViewModel(),
    windowSizeState: WindowSizeState,
    onLogout: () -> Unit
) {
    val userState by userViewModel.uiState.collectAsState()
    
    val user = (userState as? UserUiState.Success)?.user
    val firstName = user?.firstName ?: "Guest"
    val lastName = user?.lastName ?: ""
    val userId = user?.id?.toString() ?: ""
    val email = user?.email ?: ""
    val profilePictureUrl = user?.profilePictureUrl

    HomeContent(
        userId = userId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        profilePictureUrl = profilePictureUrl,
        onLogoutClick = {
            vm.logout(onLogoutSuccess = onLogout)
        }
    )
}