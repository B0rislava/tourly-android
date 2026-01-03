package com.tourly.app.core.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.AddCircle

enum class BottomNavDestination(
    val icon: ImageVector,
    val label: String
) {
    TRAVELER_HOME(Icons.Filled.Home, "Home"),
    GUIDE_HOME(Icons.Filled.Home, "Home"),
    TRAVELER_DASHBOARD(Icons.Filled.Dashboard, "Dashboard"),
    GUIDE_DASHBOARD(Icons.Filled.Dashboard, "Dashboard"),
    CREATE_TOUR(Icons.Filled.AddCircle, "Create Tour"),
    CHAT(Icons.AutoMirrored.Filled.Chat, "Chat"),
    PROFILE(Icons.Filled.Person, "Profile");

    companion object {
        val travelerDestinations = listOf(TRAVELER_HOME, TRAVELER_DASHBOARD, CHAT, PROFILE)
        val guideDestinations = listOf(GUIDE_HOME, GUIDE_DASHBOARD, CREATE_TOUR, CHAT, PROFILE)
    }
}

@Composable
fun BottomNavigationBar(
    selectedDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    destinations: List<BottomNavDestination>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor
    ) {
        destinations.forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}
