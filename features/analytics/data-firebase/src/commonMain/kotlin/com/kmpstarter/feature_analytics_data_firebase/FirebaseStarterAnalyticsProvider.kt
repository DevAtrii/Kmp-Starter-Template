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

package com.kmpstarter.feature_analytics_data_firebase

import com.kmpstarter.feature_analytics_domain.AppEvent
import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProvider
import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProviderId
import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProviderIds
import com.kmpstarter.utils.datastore.AppDataStore
import com.kmpstarter.utils.logging.Log
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.jvm.JvmInline

@JvmInline
value class FirebaseAnalyticsEnabled(val value: Boolean)

@OptIn(ExperimentalAtomicApi::class)
class FirebaseStarterAnalyticsProvider(
    private val analytics: FirebaseAnalytics,
    enabled: FirebaseAnalyticsEnabled,
) : StarterAnalyticsProvider {
    companion object {
        private const val TAG = "FirebaseAnalyticsProvider"
    }


    private val _isEnabled = AtomicBoolean(enabled.value)
    override val isEnabled get() = _isEnabled.load()


    override val id: StarterAnalyticsProviderId
        get() = StarterAnalyticsProviderIds.Firebase

    override suspend fun track(event: AppEvent) {
        track(event = event.event, properties = event.properties)
    }

    override suspend fun track(event: String) {
        track(event = event, properties = null)
    }

    override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) {
        val map = pair?.let { pair -> mapOf(pair.first to pair.second) }
        track(event = event, properties = map)
    }

    override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    ) {
        if (!isEnabled) {
            Log.i(TAG, "FIREBASE ANALYTICS ARE DISABLED")
            return
        }
        analytics.logEvent(
            name = event,
            parameters = properties
        )
    }

    override suspend fun setUserId(userId: String) {
        analytics.setUserId(userId)
    }

    override suspend fun optIn() {
        analytics.setAnalyticsCollectionEnabled(true)
        _isEnabled.store(true)
    }

    override suspend fun optOut() {
        analytics.setAnalyticsCollectionEnabled(false)
        _isEnabled.store(false)
    }

    override suspend fun toggleOptInOut() {
        if (hasOptedIn()) optOut() else optIn()
    }

    override suspend fun hasOptedIn(): Boolean {
        return isEnabled
    }

    override suspend fun flush() {
        Log.i(TAG, "Firebase Analytics doesn't support flushing manually")
    }

    override suspend fun reset() {
        analytics.resetAnalyticsData()
    }

    override suspend fun setUserProperty(key: String, value: String) {
        analytics.setUserProperty(name = key, value = value)
    }

}