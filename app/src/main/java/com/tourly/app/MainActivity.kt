package com.tourly.app

import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.core.navigation.NavigationRoot
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.tourly.app.core.domain.model.AppLanguage
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            
            val isDarkTheme = when(val state = uiState) {
                is MainActivityUiState.Success -> {
                    when (state.themeMode) {
                        ThemeMode.LIGHT -> false
                        ThemeMode.DARK -> true
                        ThemeMode.SYSTEM -> isSystemInDarkTheme()
                    }
                }
                else -> isSystemInDarkTheme()
            }

            val appLanguage = (uiState as? MainActivityUiState.Success)?.appLanguage ?: AppLanguage.ENGLISH
            val currentContext = LocalContext.current
            val currentConfiguration = LocalConfiguration.current
            
            val localizedContext = remember(appLanguage) {
                val locale = Locale.forLanguageTag(appLanguage.code)
                val config = Configuration(currentConfiguration)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                
                val contextWithConfig = currentContext.createConfigurationContext(config)
                
                object : ContextWrapper(currentContext) {
                    override fun getResources(): Resources = contextWithConfig.resources
                }
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration
            ) {
                TourlyTheme(darkTheme = isDarkTheme) {
                    NavigationRoot(viewModel = viewModel)
                }
            }
        }
    }
}
