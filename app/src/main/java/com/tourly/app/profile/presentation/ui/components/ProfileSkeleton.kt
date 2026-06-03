package com.tourly.app.profile.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tourly.app.core.presentation.ui.components.foundation.shimmerEffect

@Composable
fun ProfileSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(top = 16.dp)
    ) {
        // --- Row 1: Avatar + Name & Stats ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).shimmerEffect())

            Spacer(modifier = Modifier.width(16.dp))

            // Name + stats column
            Column(verticalArrangement = Arrangement.Center) {
                // Name line
                Box(modifier = Modifier.width(140.dp).height(22.dp).clip(RoundedCornerShape(6.dp)).shimmerEffect())
                Spacer(modifier = Modifier.height(10.dp))
                // Stats row (tours + followers)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Box(modifier = Modifier.width(30.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(70.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                    Column {
                        Box(modifier = Modifier.width(30.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(60.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                }
            }
        }

        // --- Row 2: Star rating + numeric rating + review count ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 5 star placeholders
            Box(modifier = Modifier.width(110.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Box(modifier = Modifier.width(32.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Box(modifier = Modifier.width(60.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Follow button placeholder ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(50.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- About Me section ---
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            // Section title
            Box(modifier = Modifier.width(80.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(modifier = Modifier.height(8.dp))
            // Bio lines
            Box(modifier = Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.85f).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.fillMaxWidth(0.65f).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Segmented control (Feed / Reviews tabs) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Tour card placeholders ---
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
    }
}
