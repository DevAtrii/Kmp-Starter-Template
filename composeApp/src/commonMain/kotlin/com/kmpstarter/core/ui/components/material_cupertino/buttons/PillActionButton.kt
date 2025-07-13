package com.kmpstarter.core.ui.components.material_cupertino.buttons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


/**
 * Individual pill-shaped action button with icon on left and text on right.
 */
@Composable
fun PillActionButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color? = null,
    color: Color? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val bgColor =
        if (enabled) backgroundColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors()
            .copy(
                containerColor = bgColor ?: color?.copy(alpha = 0.1f)
                ?: ButtonDefaults.filledTonalButtonColors().containerColor,
                contentColor = color ?: ButtonDefaults.filledTonalButtonColors().contentColor
            ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(
            modifier = Modifier.size(4.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
        )
    }
}