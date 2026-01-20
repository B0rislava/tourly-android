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
import com.tourly.app.core.presentation.ui.SplashScreen

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
            
            // Redirect Guide to GuideMain if they end up on TravelerMain (e.g. after login)
            LaunchedEffect(state, backStack.lastOrNull()) {
                if (state is MainActivityUiState.Success &&
                    state.isUserLoggedIn &&
                    state.userRole == UserRole.GUIDE &&
                    backStack.lastOrNull() == Route.TravelerMain
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
                            onNavigateToSettings = {
                                backStack.add(Route.Settings)
                            },
                            onTourClick = { tourId ->
                                backStack.add(Route.TourDetails(tourId))
                            }
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
                            onNavigateToSettings = {
                                backStack.add(Route.Settings)
                            },
                                onTourClick = { tourId ->
                                backStack.add(Route.TourDetails(tourId))
                            }
                        )
                    }
                }
                is Route.Settings -> {
                    NavEntry(key) {
                        SettingsScreen(
                            onNavigateBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                is Route.TourDetails -> {
                    NavEntry(key) {
                        val viewModel = hiltViewModel<TourDetailsViewModel>()
                        val tourId = key.tourId
                        LaunchedEffect(tourId) {
                            viewModel.loadTour(tourId)
                        }
                        
                        TourDetailsScreen(
                            viewModel = viewModel,
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
