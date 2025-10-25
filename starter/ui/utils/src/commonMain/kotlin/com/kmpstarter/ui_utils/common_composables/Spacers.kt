/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.ui_utils.common_composables

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.kmpstarter.ui_utils.theme.Dimens

@Composable
fun HorizontalSpacer(width: Dp = Dimens.paddingSmall) {
    Spacer(
        modifier = Modifier.width(width)
    )
}

@Composable
fun VerticalSpacer(height: Dp = Dimens.paddingSmall) {

    Spacer(
        modifier = Modifier.height(height)
    )

}

@Composable
fun ColumnScope.VerticalSpacer(weight: Float) {

    Spacer(
        modifier = Modifier.weight(weight)
    )

}