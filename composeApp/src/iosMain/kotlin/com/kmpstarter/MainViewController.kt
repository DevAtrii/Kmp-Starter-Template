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

package com.kmpstarter

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.kmpstarter.core.di.initKoin
import com.kmpstarter.core.initKmpApp
import com.kmpstarter.feature_purchases.initRevenueCat
import com.kmpstarter.core.store.KmpInAppReview
import com.kmpstarter.ui_utils.composition_locals.LocalInAppReview

private val kmpInAppReview = KmpInAppReview()

fun mainViewController() = ComposeUIViewController(
    configure = {
        initKmpApp()
        initKoin()
        initRevenueCat()
    }
) {
    CompositionLocalProvider(LocalInAppReview provides kmpInAppReview) {
        App()
    }
}