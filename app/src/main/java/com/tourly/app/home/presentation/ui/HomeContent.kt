package com.tourly.app.home.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeContent(
    userId: String,
    email: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = "Welcome, $email!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )
        androidx.compose.material3.Text(
            text = "User ID: $userId",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}