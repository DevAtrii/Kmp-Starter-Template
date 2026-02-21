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
import interop.MixPanelBridge
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@OptIn(ExperimentalForeignApi::class)
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EventsTrackerImpl(
    private val mixPanelBridge: MixPanelBridge,
) :  EventsTracker {


    init {
        mixPanelBridge.setEnabled(this.isEnabled)
    }

    actual override suspend fun track(event: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixPanelBridge.trackWithEvent(event = event)
    }

    actual override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixPanelBridge.trackWithEvent(
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
        mixPanelBridge.trackWithEvent(
            event = event,
            properties = properties as Map<Any?, *>?
        )
        flush()
    }

    actual override suspend fun setUserId(userId: String) = withContext(Dispatchers.IO) {
        if (!isEnabled)
            return@withContext
        mixPanelBridge.setUserId(userId = userId)
    }

    actual override suspend fun optIn() = withContext(Dispatchers.IO) {
        mixPanelBridge.optIn()
    }

    actual override suspend fun optOut() = withContext(Dispatchers.IO) {
        mixPanelBridge.optOut()
    }

    actual override suspend fun toggleOptInOut() = withContext(Dispatchers.IO) {
        mixPanelBridge.toggleOptInOut()
    }

    actual override suspend fun hasOptedIn(): Boolean {
        return !mixPanelBridge.hasOptedIn()
    }

    actual override suspend fun flush() = withContext(Dispatchers.IO) {
        mixPanelBridge.flush()
    }

    actual override suspend fun reset() = withContext(Dispatchers.IO) {
        mixPanelBridge.reset()
    }
}