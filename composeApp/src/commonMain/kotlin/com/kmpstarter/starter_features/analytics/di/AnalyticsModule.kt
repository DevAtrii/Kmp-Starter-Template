/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.starter_features.analytics.di

import com.kmpstarter.starter_features.analytics.data.services.AppEventsTrackerImpl
import com.kmpstarter.starter_features.analytics.domain.services.AppEventsTracker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformAnalyticsModule: Module


val analyticsModule = module {
    includes(
        platformAnalyticsModule
    )
    singleOf(::AppEventsTrackerImpl).bind<AppEventsTracker>()
}