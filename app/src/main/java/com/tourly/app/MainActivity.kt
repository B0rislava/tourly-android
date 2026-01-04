package com.tourly.app

import com.tourly.app.core.navigation.NavigationRoot
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourlyTheme() {
                NavigationRoot()
            }
        }
    }
}
