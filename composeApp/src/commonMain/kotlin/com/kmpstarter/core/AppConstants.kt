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

object AppConstants {
    val IS_DEBUG = isPlatformBuildDebug
    const val REVENUE_CAT_API_KEY = "goog"
    const val GOOGLE_WEB_CLIENT_ID =
        "1061850765240-35h84f96u3jdbmk2sqgi9d1jp28jsfif.apps.googleusercontent.com"

    const val MIXPANEL_API_TOKEN = "add-your-mixpanel-token-here"
}


// always false on ios
expect val isPlatformBuildDebug: Boolean