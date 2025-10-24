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

import com.kmpstarter.BuildConfig
import com.kmpstarter.core.AppConstants
import com.kmpstarter.starter_features.analytics.data.services.EventsTrackerImpl
import com.kmpstarter.starter_features.analytics.domain.services.EventsTracker
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single {
        val mixpanelAPI =  MixpanelAPI.getInstance(
            get(),
            AppConstants.MIXPANEL_API_TOKEN,
            true
        ).apply {
            setEnableLogging(BuildConfig.DEBUG)
            flushBatchSize = 3
        }
        mixpanelAPI
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}