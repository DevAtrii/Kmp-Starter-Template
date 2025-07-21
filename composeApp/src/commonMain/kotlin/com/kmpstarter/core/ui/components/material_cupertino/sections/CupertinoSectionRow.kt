package com.kmpstarter.core.ui.components.material_cupertino.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kmpstarter.core.ui.components.material_cupertino.dropdown.CupertinoDropdownMenu
import com.kmpstarter.core.ui.components.material_cupertino.switchs.CupertinoSwitch
import com.kmpstarter.theme.Dimens

@Composable
fun CupertinoSectionRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String = "",
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    isLast: Boolean = false,
    isClickable: Boolean = false,
    showChevron: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {},
) {
    CupertinoSectionRow(
        modifier = modifier,
        label = label,
        value = value,
        icon = icon,
        iconTint = iconTint,
        isLast = isLast,
        isClickable = isClickable,
        showChevron = showChevron,
        valueColor = valueColor,
        showSwitch = false,
        onClick = onClick,
    )
}

@Composable
fun CupertinoSectionRow(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    isLast: Boolean = false,
    isSwitchChecked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
) {
    CupertinoSectionRow(
        modifier = modifier,
        label = label,
        icon = icon,
        iconTint = iconTint,
        isLast = isLast,
        isClickable = true,
        showSwitch = true,
        isSwitchChecked = isSwitchChecked,
        onSwitchChange = onSwitchChange,
        onClick = {
            onSwitchChange(!isSwitchChecked)
        },
    )
}

/**
 * iOS-style row component for section content especially for cupertino dropdown.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupertinoSectionRow(
    modifier: Modifier = Modifier,
    dropdownMenuModifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onPopupShown: () -> Unit = {},
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    isLast: Boolean = false,
    content: @Composable ExposedDropdownMenuBoxScope.() -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier.fillMaxWidth(),
        expanded = isExpanded,
        onExpandedChange = onExpandedChange
    ) {
        CupertinoSectionRow(
            label = label,
            value = value,
            valueColor = valueColor,
            icon = icon,
            iconTint = iconTint,
            isClickable = true,
            showChevron = true,
            isLast = isLast,
            onClick = {
                onPopupShown()
                onExpandedChange(true)
            }
        )

        CupertinoDropdownMenu(
            modifier = dropdownMenuModifier.padding(
                horizontal = Dimens.paddingSmall
            ),
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            content = content
        )
    }
}

/**
 * iOS-style row component for section content.
 */
@Composable
private fun CupertinoSectionRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String = "",
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    isLast: Boolean = false,
    isClickable: Boolean = false,
    showChevron: Boolean = false,
    isSwitchChecked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    showSwitch: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {


        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    enabled = isClickable
                ) {
                    if (isClickable) {
                        onClick()
                    }
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(12.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value.isNotEmpty())
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = valueColor,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                if (showSwitch) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CupertinoSwitch(
                        checked = isSwitchChecked,
                        onCheckedChange = onSwitchChange
                    )
                }

                // Chevron for clickable rows
                if (showChevron) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Divider (except for last item)
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 48.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}