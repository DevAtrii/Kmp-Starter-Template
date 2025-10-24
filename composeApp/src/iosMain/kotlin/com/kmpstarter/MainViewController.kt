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
import com.kmpstarter.core.firebase.auth.AuthUtils
import com.kmpstarter.core.purchases.initRevenueCat
import com.kmpstarter.core.store.KmpInAppReview
import com.kmpstarter.core.store.LocalInAppReview

private val kmpInAppReview = KmpInAppReview()

fun mainViewController() = ComposeUIViewController(
    configure = {
        AuthUtils.initGoogleAuthProvider()
        initKoin()
        initRevenueCat()
    }
) {
    CompositionLocalProvider(LocalInAppReview provides kmpInAppReview) {
        App()
    }
}