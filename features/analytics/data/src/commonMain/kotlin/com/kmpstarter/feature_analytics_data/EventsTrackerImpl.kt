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

import com.kmpstarter.feature_analytics_domain.AnalyticsProvider
import com.kmpstarter.feature_analytics_domain.AnalyticsProviderId
import com.kmpstarter.feature_analytics_domain.AppEvent


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class EventsTrackerImpl : AnalyticsProvider {
    override val id: AnalyticsProviderId
    override val isEnabled: Boolean
    override suspend fun track(event: AppEvent)
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
