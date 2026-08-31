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

package com.kmpstarter.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper


fun Context.findActivity(): Activity? {
    val context = this
    if (context is ContextWrapper) {
        return context as? Activity ?: context.baseContext.findActivity()
    }

    return null
}

fun Context.requireActivity(): Activity = findActivity()
    ?: error(
        "Activity context is required. Use LocalContext.current from an Composable, " +
                "or provide an Activity-derived Context. Application context is not supported."
    )
