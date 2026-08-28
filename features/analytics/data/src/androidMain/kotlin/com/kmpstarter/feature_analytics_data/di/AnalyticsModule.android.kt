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
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


actual val platformAnalyticsModule = module {
    single {
        val mixpanelAPI = MixpanelAPI.getInstance(
            get(),
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
        mixpanelAPI
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}