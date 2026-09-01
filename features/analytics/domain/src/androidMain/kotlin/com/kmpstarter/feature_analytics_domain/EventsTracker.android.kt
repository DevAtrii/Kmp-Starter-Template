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

package com.kmpstarter.feature_analytics_domain

import com.kmpstarter.feature_analytics_domain.referrer.InstallReferrerTracker
import com.kmpstarter.utils.StarterAndroidProvider
import com.kmpstarter.utils.requireApplication
import com.kmpstarter.utils.starter.AndroidOnlyStarterApi

@AndroidOnlyStarterApi
actual suspend fun EventsTracker.trackInstallAttribution() {
    val application = StarterAndroidProvider.requireApplication()
    val referrer = InstallReferrerTracker.create()
    referrer.capture(application)
}



















