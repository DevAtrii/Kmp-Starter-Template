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

package com.kmpstarter.feature_analytics.di

import com.kmpstarter.core.KmpStarter
import com.kmpstarter.feature_analytics.data.services.EventsTrackerImpl
import com.kmpstarter.feature_analytics.domain.services.EventsTracker
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAnalyticsModule = module {
    single {
        val mixpanelAPI =  MixpanelAPI.getInstance(
            get(),
            KmpStarter.MIXPANEL_API_KEY,
            true
        ).apply {
            setEnableLogging(KmpStarter.IS_DEBUG)
            flushBatchSize = 3
        }
        mixpanelAPI
    }
    singleOf(::EventsTrackerImpl).bind<EventsTracker>()
}