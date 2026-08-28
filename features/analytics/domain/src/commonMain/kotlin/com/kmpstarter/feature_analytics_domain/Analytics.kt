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

/**
 * App-wide analytics facade.
 *
 * Register backends in [initAnalytics] — Mixpanel is not included until you pass
 * it. Koin binds this as both [Analytics] and [EventsTracker].
 *
 * - [provider] returns one backend and bypasses global routing.
 * - [combine] returns a fixed composite that does not follow later swaps.
 * - [setActiveProviders] atomically reroutes future calls on this same instance.
 * - Empty [setActiveProviders] / [combine] disables tracking for that surface.
 * - Unknown ids and duplicate ids fail fast.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("Analytics", exact = true)
interface Analytics : EventsTracker {
    companion object

    val availableProviders: List<AnalyticsProviderId>
    val activeProviders: List<AnalyticsProviderId>

    fun provider(id: AnalyticsProviderId): AnalyticsProvider

    fun combine(vararg ids: AnalyticsProviderId): EventsTracker

    fun setActiveProviders(vararg ids: AnalyticsProviderId)
}

fun Analytics.Companion.get(vararg providers: AnalyticsProvider): Analytics {
    return AnalyticsRouter.create(providers.toList())
}
