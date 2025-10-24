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

package com.kmpstarter.starter_features.analytics.domain.services

import com.kmpstarter.core.AppConstants


interface EventsTracker {

    val isEnabled: Boolean
        get() = !AppConstants.IS_DEBUG

    suspend fun track(
        event: String,
    )

    suspend fun track(
        event: String,
        pair: Pair<String, Any>? = null,
    )

    suspend fun track(
        event: String,
        properties: Map<String, Any>? = null,
    )

    suspend fun setUserId(userId: String)

    suspend fun optIn()

    suspend fun optOut()

    suspend fun toggleOptInOut()
    suspend fun hasOptedIn(): Boolean

    suspend fun flush()

    suspend fun reset()
}