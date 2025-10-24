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

package com.kmpstarter.starter_features.analytics.data.services

import com.kmpstarter.starter_features.analytics.domain.services.AppEventsTracker
import com.kmpstarter.starter_features.analytics.domain.services.EventsTracker

class AppEventsTrackerImpl(
    private val eventsTracker: EventsTracker,
) : AppEventsTracker {
    override suspend fun dummyEvent(foo: String) {
        eventsTracker.track(
            event = AppEventsTracker.DUMMY_EVENT,
            pair = "foo" to foo
        )
    }


}