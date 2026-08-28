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

package com.kmpstarter.feature_analytics_data

import com.kmpstarter.core.platform.platform
import com.kmpstarter.utils.logging.Log


object MixPanelAnalyticsScope {
    var logging: Boolean = platform.debug
    var flushInterval: Int = 3
    var enabled: Boolean = true
    var trackAutomaticEvents: Boolean = true
    internal lateinit var apiKey: String

    internal fun isInitialized(): Boolean {
        return ::apiKey.isInitialized
    }
}

fun initMixPanel(
    apiKey: String,
    configure: MixPanelAnalyticsScope.() -> Unit = {},
) {
    if (MixPanelAnalyticsScope.isInitialized()) {
        Log.i("AnalyticsScope", "analytics apiKey already initialised")
        return
    }
    MixPanelAnalyticsScope.apiKey = apiKey
    MixPanelAnalyticsScope.apply(configure)
}




















