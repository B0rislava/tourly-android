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
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Delete
import com.tourly.app.core.presentation.ui.components.TourlyAlertDialog
import androidx.compose.ui.res.stringResource
import com.tourly.app.R
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.tourly.app.core.domain.model.AppLanguage
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
    currentLanguage: AppLanguage,
    onNavigateBack: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
    onNavigateProfileDetails: () -> Unit,
    onNavigatePassword: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onDeleteAccount: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }


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

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = {
                onSetLanguage(it)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false }
        )
    }

    if (showLogoutConfirmation) {
        TourlyAlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            onConfirm = {
                onLogout()
                showLogoutConfirmation = false
            },
            title = stringResource(id = R.string.logout),
            text = stringResource(id = R.string.logout_confirmation_text),
            confirmButtonText = stringResource(id = R.string.logout)
        )
    }

    if (showDeleteConfirmation) {
        TourlyAlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            onConfirm = {
                onDeleteAccount()
                showDeleteConfirmation = false
            },
            title = stringResource(id = R.string.delete_account),
            text = stringResource(id = R.string.delete_account_confirmation),
            confirmButtonText = stringResource(id = R.string.delete),
            isDestructive = true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    text = stringResource(id = R.string.profile_settings),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Outlined.Person,
                        title = stringResource(id = R.string.profile_details),
                        onClick = onNavigateProfileDetails
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Lock,
                        title = stringResource(id = R.string.password),
                        onClick = onNavigatePassword
                    )
                }
            }


            item {
                Text(
                    text = stringResource(id = R.string.preferences),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsGroup {
                    SettingsItem(
                        icon = Icons.Outlined.Notifications,
                        title = stringResource(id = R.string.notifications),
                        onClick = onNavigateNotifications
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.DarkMode,
                        title = stringResource(id = R.string.theme),
                        onClick = { showThemeDialog = true },
                        showNavigateIcon = true,
                        trailingContent = {
                            Text(
                                text = when(themeMode) {
                                    ThemeMode.SYSTEM -> stringResource(id = R.string.system_default)
                                    ThemeMode.LIGHT -> stringResource(id = R.string.light)
                                    ThemeMode.DARK -> stringResource(id = R.string.dark)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = OutfitFamily
                            )
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = stringResource(id = R.string.language),
                        onClick = { showLanguageDialog = true },
                         trailingContent = {
                            Text(
                                text = when(currentLanguage) {
                                    AppLanguage.ENGLISH -> stringResource(id = R.string.english)
                                    AppLanguage.BULGARIAN -> stringResource(id = R.string.bulgarian)
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
                FilledTonalButton(
                    onClick = { showLogoutConfirmation = true },
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
                        text = stringResource(id = R.string.logout),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = OutfitFamily
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { showDeleteConfirmation = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.delete_account),
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
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(id = R.string.choose_language),
                fontFamily = OutfitFamily
            )
        },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    ThemeOption(
                        text = when (language) {
                            AppLanguage.ENGLISH -> stringResource(id = R.string.english)
                            AppLanguage.BULGARIAN -> stringResource(id = R.string.bulgarian)
                        },
                        selected = language == currentLanguage,
                        onClick = { onLanguageSelected(language) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel), fontFamily = OutfitFamily)
            }
        }
    )
}

@Composable
fun ThemeSelectionDialog(

    currentMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.choose_theme), fontFamily = OutfitFamily) },
        text = {
            Column {
                ThemeOption(
                    text = stringResource(id = R.string.system_default),
                    selected = currentMode == ThemeMode.SYSTEM,
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) }
                )
                ThemeOption(
                    text = stringResource(id = R.string.light),
                    selected = currentMode == ThemeMode.LIGHT,
                    onClick = { onThemeSelected(ThemeMode.LIGHT) }
                )
                ThemeOption(
                    text = stringResource(id = R.string.dark),
                    selected = currentMode == ThemeMode.DARK,
                    onClick = { onThemeSelected(ThemeMode.DARK) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(id = R.string.cancel), fontFamily = OutfitFamily)
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