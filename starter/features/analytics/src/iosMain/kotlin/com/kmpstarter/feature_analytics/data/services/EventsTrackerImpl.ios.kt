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

@file:OptIn(ExperimentalForeignApi::class)

package com.kmpstarter.feature_analytics.data.services

import cocoapods.Mixpanel.Mixpanel
import com.kmpstarter.feature_analytics.domain.services.EventsTracker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EventsTrackerImpl(
    private val mixpanel: Mixpanel,
) : EventsTracker {
    actual override suspend fun track(event: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanel.track(event)
    }

    actual override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanel.track(
            event = event,
            properties = pair?.let { mapOf(it.first to it.second) },
        )
        flush()
    }

    actual override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    ) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanel.track(
            event = event,
            properties = properties as Map<Any?, *>?
        )
        flush()
    }

    actual override suspend fun setUserId(userId: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanel.identify(distinctId = userId)
    }

    actual override suspend fun optIn() = withContext(Dispatchers.IO) {
        mixpanel.optInTracking()
    }

    actual override suspend fun optOut() = withContext(Dispatchers.IO) {
        mixpanel.optOutTracking()
    }

    actual override suspend fun toggleOptInOut() = withContext(Dispatchers.IO) {
        if (mixpanel.hasOptedOutTracking())
            optIn()
        else
            optOut()
    }

    actual override suspend fun hasOptedIn(): Boolean {
        return !mixpanel.hasOptedOutTracking()
    }

    actual override suspend fun flush() = withContext(Dispatchers.IO) {
        mixpanel.flush()
    }

    actual override suspend fun reset() = withContext(Dispatchers.IO) {
        mixpanel.reset()
    }
}