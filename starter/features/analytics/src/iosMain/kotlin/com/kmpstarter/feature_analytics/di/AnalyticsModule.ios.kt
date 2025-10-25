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

import cocoapods.Mixpanel.Mixpanel
import com.kmpstarter.core.KmpStarter
import com.kmpstarter.feature_analytics.data.services.EventsTrackerImpl
import com.kmpstarter.feature_analytics.domain.services.EventsTracker
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single {
        Mixpanel.sharedInstanceWithToken(
            apiToken = KmpStarter.MIXPANEL_API_KEY,
            trackAutomaticEvents = true,
        ).apply {
            this.enableLogging = KmpStarter.IS_DEBUG
            flushInterval = 3u
        }
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}