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

import com.kmpstarter.feature_analytics_domain.EventsTracker
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EventsTrackerImpl(
    private val mixpanelAPI: MixpanelAPI,
) :  EventsTracker {
    actual override suspend fun track(event: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanelAPI.track(event)
        flush()
    }

    actual override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        val props = JSONObject()
        if (pair != null) {
            props.put(
                pair.first,
                pair.second
            )
        }
        mixpanelAPI.track(event, props)
        flush()
    }

    actual override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    ) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        val props = JSONObject()
        properties?.forEach { (key, value) -> props.put(key, value) }
        mixpanelAPI.track(event, props)
        flush()
    }

    actual override suspend fun setUserId(userId: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixpanelAPI.identify(userId)
    }

    actual override suspend fun optIn() = withContext(Dispatchers.IO) {
        mixpanelAPI.optInTracking()
    }

    actual override suspend fun optOut() = withContext(Dispatchers.IO) {
        mixpanelAPI.optOutTracking()
    }

    actual override suspend fun toggleOptInOut() {
        if (mixpanelAPI.hasOptedOutTracking())
            optIn()
        else
            optOut()
    }

    actual override suspend fun hasOptedIn(): Boolean {
        return !mixpanelAPI.hasOptedOutTracking()
    }

    actual override suspend fun flush() = withContext(Dispatchers.IO) {
        mixpanelAPI.flush()
    }

    actual override suspend fun reset() = withContext(Dispatchers.IO) {
        mixpanelAPI.reset()
    }
}