package com.tourly.app.core.navigation

import androidx.compose.animation.ExperimentalAnimationApi
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationRoot() {
    val windowSizeState = rememberWindowSizeState()

    val backStack = rememberNavBackStack(Route.Welcome)

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
                                // TODO: Navigate to Home
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
                is Route.Home -> {
                    NavEntry(key) {
                        val viewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory> { factory ->
                            factory.create(key)
                        }
                        HomeScreen(vm = viewModel, windowSizeState = windowSizeState)
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}