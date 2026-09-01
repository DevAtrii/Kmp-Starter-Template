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

import com.kmpstarter.feature_analytics_domain.AnalyticsScope.scope
import com.kmpstarter.utils.starter.AndroidOnlyStarterApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin

/**
 * Declares which [StarterAnalyticsProvider]s the app uses.
 *
 * Call [initAnalytics] after Koin and after each provider's own init
 * (e.g. `initMixPanel`). Library consumers pass only the backends they want.
 *
 * ```kotlin
 * initMixPanel(apiKey = token) { logging = platform.debug }
 * initAnalytics {
 *     enableInstallAttribution = true
 *     providers(MixPanelAnalyticsScope.getProvider())
 * }
 * ```
 */
object AnalyticsScope {
    private val registeredProviders = mutableListOf<StarterAnalyticsProvider>()
    internal var isInitialized: Boolean = false
        private set
    internal val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * When `true`, [initAnalytics] launches [EventsTracker.trackInstallAttribution]
     * on [scope] after providers are registered.
     *
     * Android-only work (Play Install Referrer). iOS no-ops. Default `false`.
     */
    @AndroidOnlyStarterApi
    var enableInstallAttribution: Boolean = false

    /**
     * Replaces the registered backend list. Call inside [initAnalytics].
     *
     * Order is kept. All passed providers start **active**. Empty vararg
     * registers none — routed [EventsTracker] calls no-op.
     *
     * Pass instances from each backend's `getProvider()` after that backend's
     * own `init*` (`initMixPanel`, `initFirebaseAnalytics`, …). Do not bind
     * them as `EventsTracker` in Koin.
     *
     * @param providers Backends to route through.
     */
    fun providers(vararg providers: StarterAnalyticsProvider) {
        registeredProviders.clear()
        registeredProviders.addAll(providers)
    }

    internal fun snapshot(): List<StarterAnalyticsProvider> = registeredProviders.toList()

    internal fun markInitialized() {
        isInitialized = true
    }
}

/**
 * Registers analytics backends with Koin and optionally captures Play install
 * attribution.
 *
 * Call **after** `initKoin` and each backend's own `init*` (`initMixPanel`,
 * `initFirebaseAnalytics`, …). Idempotent — later calls return immediately.
 *
 * ```kotlin
 * initMixPanel(apiKey = token) { logging = platform.debug }
 * initFirebaseAnalytics { enabled = true }
 * initAnalytics {
 *     enableInstallAttribution = true
 *     providers(
 *         MixPanelAnalyticsScope.getProvider(),
 *         FirebaseAnalyticsScope.getProvider(),
 *     )
 * }
 * ```
 *
 * @param configure DSL for [AnalyticsScope.providers] and
 *   [AnalyticsScope.enableInstallAttribution].
 */
@OptIn(AndroidOnlyStarterApi::class)
fun initAnalytics(
    configure: AnalyticsScope.() -> Unit = {},
) {
    if (AnalyticsScope.isInitialized) return
    AnalyticsScope.apply(configure)
    AnalyticsScope.markInitialized()
    getKoin().get<Analytics>()

    if (!AnalyticsScope.enableInstallAttribution) return
    val eventsTracker = getKoin().get<EventsTracker>()
    scope.launch {
        eventsTracker.trackInstallAttribution()
    }
}
