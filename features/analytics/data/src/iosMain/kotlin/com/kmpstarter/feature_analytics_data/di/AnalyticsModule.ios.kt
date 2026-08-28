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

package com.kmpstarter.feature_analytics_data.di


import com.kmpstarter.feature_analytics_data.MixPanelAnalyticsScope
import com.kmpstarter.feature_analytics_data.EventsTrackerImpl
import com.kmpstarter.feature_analytics_domain.EventsTracker
import interop.MixPanelBridge
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

@OptIn(ExperimentalForeignApi::class)
actual val platformAnalyticsModule = module {
    single {
        val mixPanelBridge = MixPanelBridge(
            token = MixPanelAnalyticsScope.apiKey,
            trackAutomaticEvents = MixPanelAnalyticsScope.trackAutomaticEvents,
            flushInterval = MixPanelAnalyticsScope.flushInterval.toLong(),
            enabled = MixPanelAnalyticsScope.enabled
        )
        mixPanelBridge
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}