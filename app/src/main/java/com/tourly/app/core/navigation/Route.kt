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
    data object TravelerMain : Route, NavKey

    @Serializable
    data object GuideMain : Route, NavKey

    @Serializable
    data class TourDetails(val tourId: Long) : Route, NavKey
    
    @Serializable
    data class EditTour(val tourId: Long) : Route, NavKey

    // Bottom Navigation Routes
    @Serializable
    data class Dashboard(val userId: String) : Route, NavKey
    
    @Serializable
    data class Chat(val userId: String) : Route, NavKey
    
    @Serializable
    data class Profile(val userId: Long) : Route, NavKey
    
    // Other Routes
    @Serializable
    data object Settings : Route, NavKey

    @Serializable
    data object Notifications : Route, NavKey

    @Serializable
    data object EditProfile : Route, NavKey

    @Serializable
    data object ChangePassword : Route, NavKey

    @Serializable
    data class GroupChat(val tourId: Long) : Route, NavKey
}