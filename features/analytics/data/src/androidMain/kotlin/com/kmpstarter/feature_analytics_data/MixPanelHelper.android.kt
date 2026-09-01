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

import android.content.Context
import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProvider
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform

actual typealias MixPanelPlatform = MixpanelAPI

context(scope: Scope)
actual fun MixPanelAnalyticsScope.get(): MixPanelPlatform {
    val mixpanelAPI = MixpanelAPI.getInstance(
        scope.get<Context>(),
        MixPanelAnalyticsScope.apiKey,
        MixPanelAnalyticsScope.trackAutomaticEvents
    ).apply {
        setEnableLogging(MixPanelAnalyticsScope.logging)
        flushBatchSize = MixPanelAnalyticsScope.flushInterval
        if (MixPanelAnalyticsScope.enabled) {
            optInTracking()
        } else {
            optOutTracking()
            flush()
        }
    }

    return mixpanelAPI
}


actual fun MixPanelAnalyticsScope.getProvider(): StarterAnalyticsProvider {
    return EventsTrackerImpl(
        mixpanelAPI = KoinPlatform.getKoin().get<MixPanelPlatform>()
    )
}