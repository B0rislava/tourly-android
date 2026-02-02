package com.tourly.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tourly.app.core.presentation.ui.GuideMainScreen
import com.tourly.app.home.presentation.ui.TourDetailsScreen
import com.tourly.app.home.presentation.viewmodel.TourDetailsViewModel
import com.tourly.app.login.domain.UserRole
import com.tourly.app.core.presentation.ui.utils.rememberWindowSizeState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tourly.app.core.presentation.ui.MainScreen
import com.tourly.app.login.presentation.ui.SignInScreen
import com.tourly.app.login.presentation.ui.SignUpScreen
import com.tourly.app.onboarding.presentation.ui.WelcomeScreen
import com.tourly.app.settings.presentation.ui.SettingsScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tourly.app.MainActivityUiState
import com.tourly.app.MainViewModel
import com.tourly.app.chat.presentation.ui.GroupChatScreen
import com.tourly.app.core.presentation.ui.SplashScreen
import com.tourly.app.core.presentation.viewmodel.UserViewModel
import com.tourly.app.create_tour.presentation.ui.EditTourScreen
import com.tourly.app.notifications.presentation.ui.NotificationScreen
import com.tourly.app.profile.presentation.ui.ProfileScreen
import com.tourly.app.settings.presentation.ui.ChangePasswordScreen
import com.tourly.app.settings.presentation.ui.EditProfileScreen

@Composable
fun NavigationRoot(
    viewModel: MainViewModel = hiltViewModel()
) {
    val windowSizeState = rememberWindowSizeState()
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is MainActivityUiState.Loading -> {
            SplashScreen()
        }
        is MainActivityUiState.Success -> {
            val startRoute = if (state.isUserLoggedIn) {
                if (state.userRole == UserRole.GUIDE) {
                    Route.GuideMain
                } else {
                    Route.TravelerMain
                }
            } else {
                Route.Welcome
            }
            
            val backStack = rememberNavBackStack(startRoute)
            val userViewModel: UserViewModel = hiltViewModel()

            LaunchedEffect(state, backStack.lastOrNull()) {
                if (state.isUserLoggedIn && state.userRole == UserRole.GUIDE && backStack.lastOrNull() == Route.TravelerMain
                ) {
                    backStack.removeLastOrNull()
                    backStack.add(Route.GuideMain)
                }
            }

            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when(key) {
                        is Route.Welcome -> {
                            NavEntry(key) {
                                WelcomeScreen(
                                    windowSizeState = windowSizeState,
                                    onGetStartedClick = {
                                        backStack.add(Route.SignUp)
                                    }
                                )
                            }
                        }
                        is Route.SignIn -> {
                            NavEntry(key) {
                                SignInScreen (
                                    onNavigateToSignUp = {
                                        backStack.add(Route.SignUp)
                                    },
                                    onLoginSuccess = {
                                        backStack.clear()
                                        backStack.add(Route.TravelerMain) 
                                    }
                                )
                            }
                        }
                        is Route.SignUp -> {
                            NavEntry(key) {
                                SignUpScreen (
                                    onNavigateToSignIn = {
                                        backStack.add(Route.SignIn)
                                    },
                                    onSignUpSuccess = {
                                        backStack.clear()
                                        backStack.add(Route.TravelerMain)
                                    }
                                )
                            }
                        }
                        is Route.TravelerMain -> {
                            NavEntry(key) {
                                MainScreen(
                                    windowSizeState = windowSizeState,
                                    onLogout = {
                                        viewModel.logout {
                                            backStack.clear()
                                            backStack.add(Route.Welcome)
                                        }
                                    },
                                    onNavigateToNotifications = {
                                        backStack.add(Route.Notifications)
                                    },
                                    onNavigateToSettings = {
                                        backStack.add(Route.Settings)
                                    },
                                    onTourClick = { tourId ->
                                        backStack.add(Route.TourDetails(tourId))
                                    },
                                    onChatClick = { tourId ->
                                        backStack.add(Route.GroupChat(tourId))
                                    },
                                    userViewModel = userViewModel
                                )
                            }
                        }
                        is Route.GuideMain -> {
                            NavEntry(key) {
                                GuideMainScreen(
                                    windowSizeState = windowSizeState,
                                    onLogout = {
                                        viewModel.logout {
                                            backStack.clear()
                                            backStack.add(Route.Welcome)
                                        }
                                    },
                                    onNavigateToNotifications = {
                                        backStack.add(Route.Notifications)
                                    },
                                    onNavigateToSettings = {
                                        backStack.add(Route.Settings)
                                    },
                                    onTourClick = { tourId ->
                                        backStack.add(Route.TourDetails(tourId))
                                    },
                                    onEditTour = { tourId ->
                                        backStack.add(Route.EditTour(tourId))
                                    },
                                    onChatClick = { tourId ->
                                        backStack.add(Route.GroupChat(tourId))
                                    },
                                    userViewModel = userViewModel
                                )
                            }
                        }
                        is Route.Settings -> {
                            NavEntry(key) {
                                SettingsScreen(
                                    onNavigateBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    onLogout = {
                                        backStack.clear()
                                        backStack.add(Route.Welcome)
                                    },
                                    onAccountDeleted = {
                                        backStack.clear()
                                        backStack.add(Route.Welcome)
                                    },
                                    onNavigatePassword = {
                                        backStack.add(Route.ChangePassword)
                                    },
                                    onNavigateToEditProfile = {
                                        backStack.add(Route.EditProfile)
                                    }
                                )
                            }
                        }
                        is Route.EditProfile -> {
                            NavEntry(key) {
                                EditProfileScreen(
                                    onNavigateBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    userViewModel = userViewModel
                                )
                            }
                        }
                        is Route.ChangePassword -> {
                            NavEntry(key) {
                                ChangePasswordScreen(
                                    onNavigateBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    userViewModel = userViewModel
                                )
                            }
                        }
                        is Route.TourDetails -> {
                            NavEntry(key) {
                                val tourViewModel = hiltViewModel<TourDetailsViewModel>()
                                val tourId = key.tourId
                                LaunchedEffect(tourId) {
                                    tourViewModel.loadTour(tourId)
                                }
                                
                                TourDetailsScreen(
                                    viewModel = tourViewModel,
                                    userRole = state.userRole,
                                    onBookingSuccess = {
                                        userViewModel.refreshBookings()
                                    },
                                    onEditTour = { id ->
                                        backStack.add(Route.EditTour(id))
                                    },
                                    onGuideClick = { guideId ->
                                        backStack.add(Route.Profile(guideId))
                                    },
                                    onBackClick = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                        }
                        is Route.EditTour -> {
                            NavEntry(key) {
                                EditTourScreen(
                                    tourId = key.tourId,
                                    onNavigateBack = {
                                        backStack.removeLastOrNull()
                                    },
                                    onUpdateSuccess = {
                                        userViewModel.showMessage("Tour updated successfully!")
                                        userViewModel.refreshBookings()
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                        }
                        is Route.Notifications -> {
                            NavEntry(key) {
                                NotificationScreen(
                                    onBackClick = { backStack.removeLastOrNull() }
                                )
                            }
                        }
                        is Route.Profile -> {
                            NavEntry(key) {
                                ProfileScreen(
                                    userId = key.userId,
                                    onSeeMore = { /*TODO*/ },
                                    onBackClick = { backStack.removeLastOrNull() }
                                )
                            }
                        }
                        is Route.GroupChat -> {
                            NavEntry(key) {
                                GroupChatScreen(
                                    tourId = key.tourId,
                                    onBackClick = {
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                        }
                        else -> error("Unknown NavKey: $key")
                    }
                }
            )
        }
    }
}
