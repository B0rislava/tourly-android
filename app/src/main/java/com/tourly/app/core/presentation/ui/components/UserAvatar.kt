package com.tourly.app.core.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import coil.request.ImageRequest
import coil.compose.SubcomposeAsyncImage
import java.util.Locale

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    name: String,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    backgroundColor: Color? = null
) {
    val context = LocalContext.current
    val initials = getInitials(name)
    
    val avatarColor = backgroundColor ?: remember(name) {
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2),
            Color(0xFF5C6BC0), Color(0xFF42A5F5), Color(0xFF29B6F6), Color(0xFF26C6DA),
            Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFF9CCC65), Color(0xFFD4E157),
            Color(0xFFFFCA28), Color(0xFFFF7043), Color(0xFF8D6E63), Color(0xFFBDBDBD),
            Color(0xFF78909C)
        )
        val index = (name.hashCode() % colors.size + colors.size) % colors.size
        colors[index]
    }

    val textColor = if (backgroundColor != null) MaterialTheme.colorScheme.onTertiary else Color.White

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(avatarColor),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNullOrBlank()) {
            Text(
                text = initials,
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "$name's avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                   Box(
                       modifier = Modifier.fillMaxSize().background(avatarColor),
                       contentAlignment = Alignment.Center
                   ) {
                       Text(
                           text = initials,
                           style = textStyle,
                           fontWeight = FontWeight.Bold,
                           color = textColor
                       )
                   }
                },
                error = {
                    Box(
                       modifier = Modifier.fillMaxSize().background(avatarColor),
                       contentAlignment = Alignment.Center
                   ) {
                       Text(
                           text = initials,
                           style = textStyle,
                           fontWeight = FontWeight.Bold,
                           color = textColor
                       )
                   }
                }
            )
        }
    }
}

private fun getInitials(name: String): String {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return ""

    val parts = trimmedName.split("\\s+".toRegex())
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(1).uppercase(Locale.getDefault())
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase(Locale.getDefault())
    }
}
