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
import com.kmpstarter.feature_analytics_data.get
import org.koin.dsl.module


actual val platformAnalyticsModule = module {
    single {
        MixPanelAnalyticsScope.get()
    }
}