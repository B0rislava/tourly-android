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
import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.tourly.app.R

enum class BottomNavDestination(
    val icon: ImageVector,
    @param:StringRes val labelResId: Int
) {
    TRAVELER_HOME(Icons.Filled.Home, R.string.home),
    GUIDE_HOME(Icons.Filled.Home, R.string.home),
    TRAVELER_DASHBOARD(Icons.Filled.Dashboard, R.string.dashboard),
    GUIDE_DASHBOARD(Icons.Filled.Dashboard, R.string.dashboard),
    CREATE_TOUR(Icons.Filled.AddCircle, R.string.create_tour),
    CHAT(Icons.AutoMirrored.Filled.Chat, R.string.chat),
    PROFILE(Icons.Filled.Person, R.string.profile);

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
            val label = stringResource(id = destination.labelResId)
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) },
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) }
            )
        }
    }
}
