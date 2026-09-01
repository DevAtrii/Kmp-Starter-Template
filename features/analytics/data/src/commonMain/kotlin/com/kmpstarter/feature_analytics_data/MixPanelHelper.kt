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

import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProvider
import com.kmpstarter.utils.logging.Log
import org.koin.core.scope.Scope


expect class MixPanelPlatform

context(scope: Scope)
expect fun MixPanelAnalyticsScope.get(): MixPanelPlatform


expect fun MixPanelAnalyticsScope.getProvider(): StarterAnalyticsProvider


object MixPanelAnalyticsScope {
    var logging: Boolean = false
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
