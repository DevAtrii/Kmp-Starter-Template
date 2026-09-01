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

/**
 * Sends analytics events and user traits to the backends registered in
 * [initAnalytics].
 *
 * Inject this in ViewModels. Prefer typed [AppEvent] over raw event names.
 * Calls fan out to every **active** backend. Disabled backends no-op.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("EventsTracker", exact = true)
interface EventsTracker {

    /** `true` when at least one active backend is collecting. */
    val isEnabled: Boolean

    /** Tracks a typed [event]. Preferred overload. */
    suspend fun track(
        event: AppEvent,
    )

    /** Tracks [event] with no properties. */
    suspend fun track(
        event: String,
    )

    /**
     * Tracks [event] with a single optional property.
     *
     * @param pair `null` → same as [track] with no properties.
     */
    suspend fun track(
        event: String,
        pair: Pair<String, Any>? = null,
    )

    /**
     * Tracks [event] with [properties].
     *
     * @param properties `null` or empty → same as [track] with no properties.
     */
    suspend fun track(
        event: String,
        properties: Map<String, Any>? = null,
    )

    /** Ties later events to this user. Call after sign-in. */
    suspend fun setUserId(userId: String)

    /** Turns collection on. */
    suspend fun optIn()

    /** Turns collection off. */
    suspend fun optOut()

    /** Flips [hasOptedIn]. */
    suspend fun toggleOptInOut()

    /** Whether the user is currently opted in. */
    suspend fun hasOptedIn(): Boolean

    /** Sends any queued events now. */
    suspend fun flush()

    /** Clears identity and local analytics state. Call on logout. */
    suspend fun reset()

    /**
     * Sets a trait on the current user (`plan`, `is_pro`, …).
     *
     * [value] may be a [String], [Boolean], [Int], [Long], or [Double].
     */
    suspend fun setUserProperty(
        key: String,
        value: Any,
    )

    /**
     * Params attached to every later [track] call (`app_flavor`, …).
     *
     * Merges with existing defaults. Omitting a key does not remove it.
     */
    suspend fun setDefaultEventParameters(
        params: Map<String, Any>,
    )
}

/** Sets many user traits. Same as calling [setUserProperty] per entry. */
suspend fun EventsTracker.setUserProperties(
    values: Map<String, Any>,
) {
    values.forEach { (key, value) ->
        setUserProperty(key = key, value = value)
    }
}

/**
 * Records where the install came from. Android only; other platforms no-op.
 *
 * Runs once. Later calls are ignored. Prefer
 * [AnalyticsScope.enableInstallAttribution] on [initAnalytics] instead of
 * calling this yourself.
 */
@AndroidOnlyStarterApi
expect suspend fun EventsTracker.trackInstallAttribution()
