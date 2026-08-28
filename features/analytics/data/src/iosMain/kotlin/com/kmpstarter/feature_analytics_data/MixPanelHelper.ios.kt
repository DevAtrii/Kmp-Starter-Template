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

import com.kmpstarter.feature_analytics_domain.AnalyticsProvider
import interop.MixPanelBridge
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform

@OptIn(ExperimentalForeignApi::class)
actual typealias MixPanelPlatform = MixPanelBridge

@OptIn(ExperimentalForeignApi::class)
context(scope: Scope)
actual fun MixPanelAnalyticsScope.get(): MixPanelPlatform {

    val mixPanelBridge = MixPanelBridge(
        token = MixPanelAnalyticsScope.apiKey,
        trackAutomaticEvents = MixPanelAnalyticsScope.trackAutomaticEvents,
        flushInterval = MixPanelAnalyticsScope.flushInterval.toLong(),
        enabled = MixPanelAnalyticsScope.enabled
    )
    return mixPanelBridge
}

@OptIn(ExperimentalForeignApi::class)
actual fun MixPanelAnalyticsScope.getProvider(): AnalyticsProvider {
    return EventsTrackerImpl(
        mixPanelBridge = KoinPlatform.getKoin().get<MixPanelPlatform>()
    )
}