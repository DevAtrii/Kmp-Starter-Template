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


@OptIn(ExperimentalObjCName::class)
@ObjCName("EventsTracker", exact = true)
interface EventsTracker {

    val isEnabled: Boolean
        get() = true

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