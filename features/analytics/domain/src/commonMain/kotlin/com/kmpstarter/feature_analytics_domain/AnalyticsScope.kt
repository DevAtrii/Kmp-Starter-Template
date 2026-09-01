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
 *     providers(MixPanelAnalyticsScope.getProvider())
 * }
 * ```
 */
object AnalyticsScope {
    private val registeredProviders = mutableListOf<StarterAnalyticsProvider>()
    internal var isInitialized: Boolean = false
        private set

    fun providers(vararg providers: StarterAnalyticsProvider) {
        registeredProviders.clear()
        registeredProviders.addAll(providers)
    }

    internal fun snapshot(): List<StarterAnalyticsProvider> = registeredProviders.toList()

    internal fun markInitialized() {
        isInitialized = true
    }
}

fun initAnalytics(configure: AnalyticsScope.() -> Unit = {}) {
    if (AnalyticsScope.isInitialized) return
    AnalyticsScope.apply(configure)
    AnalyticsScope.markInitialized()
    getKoin().get<Analytics>()
}
