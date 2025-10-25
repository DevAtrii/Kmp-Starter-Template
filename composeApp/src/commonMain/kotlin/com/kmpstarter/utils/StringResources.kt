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

package com.kmpstarter.utils

import kmpstarter.composeapp.generated.resources.Res
import kmpstarter.composeapp.generated.resources.empty_string
import org.jetbrains.compose.resources.StringResource

object KMPStringResources {
    val empty = Res.string.empty_string
}


fun StringResource.isEmpty() = this == Res.string.empty_string