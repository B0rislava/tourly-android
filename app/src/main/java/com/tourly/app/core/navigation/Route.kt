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
    data object Home : Route, NavKey
    
    // Bottom Navigation Routes
    @Serializable
    data class Dashboard(val userId: String) : Route, NavKey
    
    @Serializable
    data class Chat(val userId: String) : Route, NavKey
    
    @Serializable
    data class Profile(val userId: String) : Route, NavKey
    
    // Other Routes
    @Serializable
    data object Settings : Route, NavKey
}