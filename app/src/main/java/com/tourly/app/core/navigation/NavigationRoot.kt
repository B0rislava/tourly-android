package com.tourly.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.tourly.app.login.presentation.ui.SignInScreen
import com.tourly.app.login.presentation.ui.SignUpScreen
import com.tourly.app.onboarding.presentation.ui.WelcomeScreen
import com.tourly.app.test.presentation.ui.TestConnectionScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot() {

    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Welcome::class, Route.Welcome.serializer())
                    subclass(Route.SignIn::class, Route.SignIn.serializer())
                    subclass(Route.SignUp::class, Route.SignUp.serializer())
                    subclass(Route.TestConnection::class, Route.TestConnection.serializer())
                }
            }
        },
        Route.Welcome
    )
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when(key) {
                is Route.Welcome -> {
                    NavEntry(key) {
                        WelcomeScreen(
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
                is Route.TestConnection -> {
                    NavEntry(key) {
                        TestConnectionScreen(
                            onNavigateBack = {
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