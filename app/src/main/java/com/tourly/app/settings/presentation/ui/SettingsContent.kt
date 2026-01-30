package com.tourly.app.settings.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Report
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.core.domain.model.User
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.settings.presentation.ui.components.SettingsGroup
import com.tourly.app.settings.presentation.ui.components.SettingsItem
import com.tourly.app.settings.presentation.ui.components.SettingsProfileCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    user: User?,
    themeMode: ThemeMode,
    onNavigateBack: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onLogout: () -> Unit,
    onNavigateProfileDetails: () -> Unit,
    onNavigatePassword: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateSupport: () -> Unit,
    onNavigateReport: () -> Unit,
    onNavigateAbout: () -> Unit,
    onNavigateLanguage: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = themeMode,
            onThemeSelected = {
                onSetThemeMode(it)
                showThemeDialog = false
            },
            onDismissRequest = { showThemeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                user?.let {
                    SettingsProfileCard(user = it)
                }
            }

            item {
                Text(
                    text = "Other Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Outlined.Person,
                        title = "Profile Details",
                        onClick = onNavigateProfileDetails
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Lock,
                        title = "Password",
                        onClick = onNavigatePassword
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications",
                        onClick = onNavigateNotifications
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.DarkMode,
                        title = "Theme",
                        onClick = { showThemeDialog = true },
                        showNavigateIcon = true, // It now navigates to a dialog conceptually
                        trailingContent = {
                            Text(
                                text = when(themeMode) {
                                    ThemeMode.SYSTEM -> "System"
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = OutfitFamily
                            )
                        }
                    )
                }
            }

            item {
                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Outlined.SupportAgent,
                        title = "Support",
                        onClick = onNavigateSupport
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Report,
                        title = "Report an Issue",
                        onClick = onNavigateReport
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Info,
                        title = "About Tourly",
                        onClick = onNavigateAbout
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = "Language",
                        onClick = onNavigateLanguage
                    )
                }
            }

            item {
                FilledTonalButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log out",
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = OutfitFamily
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Choose Theme", fontFamily = OutfitFamily) },
        text = {
            Column {
                ThemeOption(
                    text = "System Default",
                    selected = currentMode == ThemeMode.SYSTEM,
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                )
                ThemeOption(
                    text = "Light",
                    selected = currentMode == ThemeMode.LIGHT,
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
                ThemeOption(
                    text = "Dark",
                    selected = currentMode == ThemeMode.DARK,
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", fontFamily = OutfitFamily)
            }
        }
    )
}

@Composable
fun ThemeOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = OutfitFamily
        )
    }
}