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

package com.kmpstarter.core

fun initKmpApp() {
    KmpStarter.initApp(
        isDebug = AppConstants.IS_DEBUG,
        revenueCatApiKey = AppConstants.REVENUE_CAT_API_KEY,
        mixPanelApiKey = AppConstants.MIXPANEL_API_TOKEN
    )
}