package com.tourly.app.core.navigation

import androidx.compose.runtime.Composable
import com.tourly.app.core.ui.utils.rememberWindowSizeState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tourly.app.home.presentation.ui.HomeScreen
import com.tourly.app.home.presentation.viewmodel.HomeViewModel
import com.tourly.app.login.presentation.ui.SignInScreen
import com.tourly.app.login.presentation.ui.SignUpScreen
import com.tourly.app.onboarding.presentation.ui.WelcomeScreen
import com.tourly.app.test.presentation.ui.TestConnectionScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tourly.app.MainActivityUiState
import com.tourly.app.MainViewModel
import com.tourly.app.core.ui.SplashScreen

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
                Route.Home(userId = "user_123", email = "test@example.com") // TODO: get real user info
            } else {
                Route.Welcome
            }
            
            val backStack = rememberNavBackStack(startRoute)

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
                            },
                            onTestConnectionClick = {
                                backStack.add(Route.TestConnection)
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
                                backStack.add(Route.Home(userId = "user_123", email = "test@example.com"))
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
                                // Clear back stack so user can't go back to auth screens
                                backStack.clear()
                                // Dummy info for testing
                                backStack.add(Route.Home(userId = "user_123", email = "test@example.com"))
                            }
                        )
                    }
                }
                is Route.TestConnection -> {
                    NavEntry(key) {
                        TestConnectionScreen(
                            onNavigateBack = {
                                backStack.removeLastOrNull()
                            }
                        )
                    }
                }
                is Route.Home -> {
                    NavEntry(key) {
                        val viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory> { factory ->
                            factory.create(key)
                        }
                        HomeScreen(
                            vm = viewModel,
                            windowSizeState = windowSizeState,
                            onLogout = {
                                backStack.clear()
                                backStack.add(Route.Welcome)
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