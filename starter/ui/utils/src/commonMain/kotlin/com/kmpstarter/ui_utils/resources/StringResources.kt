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

package com.kmpstarter.ui_utils.resources

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun StringResource.toActualString() = stringResource(resource = this)

@Suppress("ParamsComparedByRef")
@Composable
fun StringResource.toActualString(vararg formatArgs: Any) =
    stringResource(resource = this, formatArgs = formatArgs)

@Composable
fun StringResource.ifThen(condition: Boolean, block: () -> StringResource) =
    stringResource(resource = if (condition) block() else this)