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

package com.kmpstarter.feature_analytics_domain

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * A concrete analytics backend (Mixpanel, Firebase, a custom SDK, …).
 *
 * Register each provider once in Koin with `bind AnalyticsProvider::class`.
 * Do not bind two providers as `single<AnalyticsProvider>` — the second overwrites the first.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("AnalyticsProvider", exact = true)
interface AnalyticsProvider : EventsTracker {
    val id: AnalyticsProviderId
}
