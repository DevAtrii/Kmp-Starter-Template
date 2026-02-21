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

package com.kmpstarter.feature_analytics_data

import com.kmpstarter.feature_analytics_domain.AppEventsTracker
import com.kmpstarter.feature_analytics_domain.EventsTracker


class AppEventsTrackerImpl(
    private val eventsTracker: EventsTracker,
) : AppEventsTracker {
    override suspend fun trackTrafficSource(source: String) {
        eventsTracker.track(
            event = AppEventsTracker.KEY_ONBOARDING_TRAFFIC_SOURCE,
            pair = "source" to source
        )
    }
}