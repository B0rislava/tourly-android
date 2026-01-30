package com.tourly.app

import com.tourly.app.core.domain.model.ThemeMode
import com.tourly.app.core.navigation.NavigationRoot
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

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

            TourlyTheme(darkTheme = isDarkTheme) {
                NavigationRoot(viewModel = viewModel)
            }
        }
    }
}
