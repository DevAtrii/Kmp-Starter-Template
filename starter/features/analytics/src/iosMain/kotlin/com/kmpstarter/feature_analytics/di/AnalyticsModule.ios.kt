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

@file:OptIn(ExperimentalForeignApi::class)

package com.kmpstarter.feature_analytics.di

import com.kmpstarter.core.KmpStarter
import com.kmpstarter.feature_analytics.data.services.EventsTrackerImpl
import com.kmpstarter.feature_analytics.domain.services.EventsTracker
import interop.MixPanelBridge
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single {
       val mixPanelBridge = MixPanelBridge(
            token =  KmpStarter.MIXPANEL_API_KEY,
            trackAutomaticEvents = true,
            flushInterval = 3L,
            enabled = true
        )
         mixPanelBridge
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}