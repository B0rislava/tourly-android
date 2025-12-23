package com.tourly.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.tourly.app.login.presentation.ui.SignInScreen
import com.tourly.app.login.presentation.ui.SignUpScreen
import com.tourly.app.onboarding.presentation.ui.WelcomeScreen

@Composable
fun NavigationRoot() {

    val backStack = rememberNavBackStack(Route.Welcome)

    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when(key) {
                is Route.Welcome -> {
                    NavEntry(key) {
                        WelcomeScreen(
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
                                // TODO: Navigate to Home
                            }
                        )
                    }
                }
                else -> error("Unknown NavKey: $key")
            }
        }
    )
}