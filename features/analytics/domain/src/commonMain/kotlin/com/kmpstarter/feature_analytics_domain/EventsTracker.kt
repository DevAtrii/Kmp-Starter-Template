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

import com.kmpstarter.utils.starter.AndroidOnlyStarterApi
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName


@OptIn(ExperimentalObjCName::class)
@ObjCName("EventsTracker", exact = true)
interface EventsTracker {

    val isEnabled: Boolean

    suspend fun track(
        event: AppEvent,
    )

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


    suspend fun setUserProperty(
        key: String,
        value: String,
    )

}


suspend fun EventsTracker.setUserProperties(
    values: Map<String, String>,
) {
    values.forEach { (key, value) ->
        setUserProperty(key = key, value = value)
    }
}



/**
 * Captures Play Install Referrer once and tracks it as `install_attribution`.
 *
 * **Android:** connects to Play Store, parses UTM / click ids from the referrer
 * string, tracks one event, then writes matching user properties
 * (`utm_source`, `gclid`, `install_version`, …). After a successful capture
 * (or `FEATURE_NOT_SUPPORTED`) later calls no-op.
 *
 * Play unavailable / timeout → no-op this launch, retries next cold start.
 * Call after Koin + [initAnalytics] so providers are registered.
 *
 * **Other platforms:** no-op. iOS has no Install Referrer API; use
 * App Store Connect / SKAdNetwork instead.
 *
 * Prefer [AnalyticsScope.enableInstallAttribution] on [initAnalytics] so this
 * runs once at app start. Safe to call manually; duplicates are ignored.
 */
@AndroidOnlyStarterApi
expect suspend fun EventsTracker.trackInstallAttribution()

















