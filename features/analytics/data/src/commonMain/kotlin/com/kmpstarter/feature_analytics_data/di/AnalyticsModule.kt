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

import com.kmpstarter.feature_analytics_data.AppEventsTrackerImpl
import com.kmpstarter.feature_analytics_domain.AppEventsTracker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformAnalyticsModule: Module


val analyticsDataModule = module {
    includes(
        platformAnalyticsModule
    )
    singleOf(::AppEventsTrackerImpl).bind<AppEventsTracker>()
}