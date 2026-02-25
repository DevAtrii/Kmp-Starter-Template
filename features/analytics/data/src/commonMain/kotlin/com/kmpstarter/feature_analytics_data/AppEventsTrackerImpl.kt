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

    override suspend fun trackPurchaseSuccess(productId: String) {
        eventsTracker.track(
            event = AppEventsTracker.KEY_PURCHASE_SUCCESS,
            pair = "productId" to productId
        )
    }

    override suspend fun trackPurchaseFailure(productId: String, error: String) {
        eventsTracker.track(
            event = AppEventsTracker.KEY_PURCHASE_FAILURE,
            properties = mapOf(
                "productId" to productId,
                "error" to error
            )
        )
    }

    override suspend fun trackPurchaseProductsFailure(error: String) {
        eventsTracker.track(
            event = AppEventsTracker.KEY_PURCHASE_PRODUCTS_FAILURE,
            pair = "error" to error
        )
    }

    override suspend fun trackPurchaseRestoreFailure(error: String) {
        eventsTracker.track(
            event = AppEventsTracker.KEY_PURCHASE_RESTORE_FAILURE,
            pair = "error" to error
        )
    }
}