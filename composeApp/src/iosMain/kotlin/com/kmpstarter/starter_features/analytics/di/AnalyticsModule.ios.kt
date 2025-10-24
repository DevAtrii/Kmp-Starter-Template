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

package com.kmpstarter.starter_features.analytics.di

import cocoapods.Mixpanel.Mixpanel
import com.kmpstarter.core.AppConstants
import com.kmpstarter.starter_features.analytics.data.services.EventsTrackerImpl
import com.kmpstarter.starter_features.analytics.domain.services.EventsTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single {
        Mixpanel.sharedInstanceWithToken(
            apiToken = AppConstants.MIXPANEL_API_TOKEN,
            trackAutomaticEvents = true,
        ).apply {
            this.enableLogging = AppConstants.IS_DEBUG
            flushInterval = 3u
        }
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}