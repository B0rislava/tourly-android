package com.tourly.app.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object Welcome: Route, NavKey

    @Serializable
    data object SignIn: Route, NavKey

    @Serializable
    data object SignUp: Route, NavKey

    @Serializable
    data object TestConnection: Route, NavKey
  
    @Serializable
    // TODO: Refactor to pass only userId. Email/User details should be fetched from Repository.
    data class Home(val userId: String, val email: String) : Route, NavKey
}