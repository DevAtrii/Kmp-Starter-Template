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

package com.kmpstarter.core.ui.utils.resources

import androidx.compose.runtime.Composable
import kmpstarter.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object KMPStringResources {
    val empty = Res.string.empty_string
}

fun StringResource.isEmpty() = this == Res.string.empty_string

fun KMPStringResources.empty() = Res.string.empty_string


@Composable
fun StringResource.toActualString() = stringResource(resource = this)

@Composable
fun StringResource.ifCondition(condition: Boolean, block: () -> StringResource) =
    stringResource(resource = if (condition) block() else this)

