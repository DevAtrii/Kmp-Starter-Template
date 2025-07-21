package com.kmpstarter.core.ui.components.material_cupertino.dropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp




/**
 * iOS-inspired dropdown menu item.
 *
 * @param text Text content of the dropdown item
 * @param onClick Callback when the item is clicked
 * @param modifier Modifier for styling
 * @param enabled Whether the item is enabled
 * @param leadingIcon Leading icon for the item
 * @param trailingIcon Trailing icon for the item (e.g., checkmark)
 * @param isSelected Whether this item is currently selected
 * @param showDivider Whether to show a divider after this item
 * @param textColor Color of the text
 * @param backgroundColor Background color when pressed
 */
@Composable
fun CupertinoDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isSelected: Boolean = false,
    showDivider: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
) {
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = enabled
                ) {
                    if (enabled) {
                        onClick()
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Text
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) textColor else textColor.copy(alpha = 0.6f),
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )

                // Trailing Icon (Checkmark for selected items)
                if (isSelected && trailingIcon == null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }

        // Divider
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}



/**
 * iOS-inspired dropdown menu item with leading and trailing icons as ImageVector.
 * 
 * @param text Text content of the dropdown item
 * @param onClick Callback when the item is clicked
 * @param modifier Modifier for styling
 * @param enabled Whether the item is enabled
 * @param leadingIcon Leading icon as ImageVector
 * @param trailingIcon Trailing icon as ImageVector
 * @param isSelected Whether this item is currently selected
 * @param showDivider Whether to show a divider after this item
 * @param leadingIconTint Color tint for the leading icon
 * @param trailingIconTint Color tint for the trailing icon
 */
@Composable
fun CupertinoDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isSelected: Boolean = false,
    showDivider: Boolean = false,
    leadingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    CupertinoDropdownItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) leadingIconTint else leadingIconTint.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        trailingIcon = trailingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) trailingIconTint else trailingIconTint.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        isSelected = isSelected,
        showDivider = showDivider,
        textColor = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * iOS-inspired dropdown menu item with leading icon as ImageVector and custom tint.
 * 
 * @param text Text content of the dropdown item
 * @param onClick Callback when the item is clicked
 * @param modifier Modifier for styling
 * @param enabled Whether the item is enabled
 * @param leadingIcon Leading icon as ImageVector
 * @param isSelected Whether this item is currently selected
 * @param showDivider Whether to show a divider after this item
 * @param leadingIconTint Color tint for the leading icon
 */
@Composable
fun CupertinoDropdownItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    isSelected: Boolean = false,
    showDivider: Boolean = false,
    leadingIconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    CupertinoDropdownItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) leadingIconTint else leadingIconTint.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        isSelected = isSelected,
        showDivider = showDivider,
        textColor = MaterialTheme.colorScheme.onSurface
    )
}

