/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.core.store

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.compositionLocalOf

expect class KmpInAppReview {
    /** for android it throws exception & for ios it skips*/
    suspend fun askForReview()
}


// composition local
@OptIn(ExperimentalMaterial3Api::class)
val LocalInAppReview = compositionLocalOf<KmpInAppReview> { error("Provide KMP In App Review") }