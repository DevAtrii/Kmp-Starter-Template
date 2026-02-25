/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarter.ui_components.chips.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FiltrationChip(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    removeFilterIcon: Boolean = true,
    enabled: Boolean = true,
    iconsEnterTransition: EnterTransition = fadeIn() + scaleIn(),
    iconsExitTransition: ExitTransition = fadeOut() + scaleOut(),
    onRemoveFilter: () -> Unit = {},
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = modifier.animateContentSize(),
        enabled = enabled,
        onClick = onClick,
        label = { Text(text = label) },
        selected = selected,
        leadingIcon = {
            AnimatedVisibility(
                visible = leadingIcon != null,
                enter = iconsEnterTransition,
                exit = iconsExitTransition
            ) {
                Icon(
                    imageVector = leadingIcon!!,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

        },
        trailingIcon = {
            AnimatedVisibility(
                visible = selected && removeFilterIcon,
                enter = iconsEnterTransition,
                exit = iconsExitTransition
            ) {
                IconButton(
                    modifier = Modifier.size(16.dp),
                    onClick = onRemoveFilter,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(
                visible = trailingIcon != null && !(selected && removeFilterIcon),
                enter = iconsEnterTransition,
                exit = iconsExitTransition
            ) {
                Icon(
                    imageVector = trailingIcon!!,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }


        }
    )

}