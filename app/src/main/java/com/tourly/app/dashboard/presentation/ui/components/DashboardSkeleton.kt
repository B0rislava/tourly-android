package com.tourly.app.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.components.foundation.shimmerEffect

@Composable
fun DashboardSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp)) {
        // Title placeholder
        Box(modifier = Modifier.fillMaxWidth(0.4f).height(32.dp).clip(RoundedCornerShape(8.dp)).shimmerEffect())
        Spacer(modifier = Modifier.height(8.dp))
        // Subtitle placeholder
        Box(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Tabs placeholder
        Box(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(16.dp)).shimmerEffect())
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Items placeholder
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
