package com.tourly.app.core.presentation.ui.components.foundation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.sp
import com.tourly.app.core.presentation.ui.theme.OutfitFamily

@Composable
fun ClickableText(
    prefixText: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        append("$prefixText ")

        withLink(
            LinkAnnotation.Clickable(
                tag = "action_tag",
                styles = TextLinkStyles(
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        ).toSpanStyle()
                ),

                linkInteractionListener = { onActionClick() }
            )
        ) {
            append(actionText)
        }
    }

    Text(
        text = annotatedString,
        fontFamily = OutfitFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
}