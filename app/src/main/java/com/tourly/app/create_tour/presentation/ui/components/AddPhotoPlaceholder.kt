package com.tourly.app.create_tour.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun AddPhotoPlaceholder() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { /* TODO: Upload images */ },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.Gray)
            Text("Add Photo", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontFamily = OutfitFamily)
        }
    }
}