package com.tourly.app.login.presentation.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun VerificationCodeDialog(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    onDismiss: () -> Unit,
    error: String? = null,
    isVerifying: Boolean = false,
    isSuccess: Boolean = false
) {
    // Blinking cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0
                1f at 499
                0f at 500
                0f at 999
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "cursorAlpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Icon instead of emoji
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email verification",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Check your email",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter the code sent to",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 6-digit code input
                BasicTextField(
                    value = code,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) onCodeChange(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    decorationBox = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(6) { index ->
                                val char = when {
                                    index >= code.length -> ""
                                    else -> code[index].toString()
                                }
                                val isFocused = code.length == index
                                
                                Box(
                                    modifier = Modifier
                                        .size(width = 45.dp, height = 55.dp)
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) MaterialTheme.colorScheme.primary 
                                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isFocused) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontFamily = OutfitFamily,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    // Blinking Cursor
                                    if (isFocused && !isVerifying && !isSuccess) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(24.dp)
                                                .alpha(cursorAlpha)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = OutfitFamily,
                        textAlign = TextAlign.Center
                    )
                }

                if (isVerifying) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }

                if (isSuccess) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Success! Account verified.",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = OutfitFamily,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Can't find the email? Check your spam folder.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = OutfitFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        fontFamily = OutfitFamily,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
