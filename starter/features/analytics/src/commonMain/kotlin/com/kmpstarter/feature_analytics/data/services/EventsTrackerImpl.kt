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

package com.kmpstarter.feature_analytics.data.services

import com.kmpstarter.feature_analytics.domain.services.EventsTracker


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class EventsTrackerImpl : EventsTracker {
    override suspend fun track(event: String)
    override suspend fun track(event: String, pair: Pair<String, Any>?)
    override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    )

    override suspend fun setUserId(userId: String)
    override suspend fun optIn()
    override suspend fun optOut()
    override suspend fun toggleOptInOut()
    override suspend fun hasOptedIn(): Boolean
    override suspend fun flush()
    override suspend fun reset()
}