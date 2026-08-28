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

import com.kmpstarter.feature_analytics_domain.AnalyticsProvider
import com.kmpstarter.feature_analytics_domain.AnalyticsProviderId
import com.kmpstarter.feature_analytics_domain.AnalyticsProviderIds
import com.kmpstarter.feature_analytics_domain.AppEvent
import com.kmpstarter.utils.datastore.AppDataStore
import com.kmpstarter.utils.datastore.booleanDataStore
import com.kmpstarter.utils.logging.Log
import dev.gitlive.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsProvider(
    private val analytics: FirebaseAnalytics,
    appDataStore: AppDataStore,
) : AnalyticsProvider {
    companion object {
        private const val TAG = "FirebaseAnalyticsProvider"
    }

    private val analyticsEnabledDs = appDataStore.booleanDataStore(
        name = "_starter_firebase_analytics_enabled",
        default = true
    )

    override val id: AnalyticsProviderId
        get() = AnalyticsProviderIds.Firebase

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
        val enabled = analyticsEnabledDs.get()!!
        if (!enabled) {
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
    }

    override suspend fun optOut() {
        analytics.setAnalyticsCollectionEnabled(false)
    }

    override suspend fun toggleOptInOut() {
        if (hasOptedIn()) optOut() else optIn()
    }

    override suspend fun hasOptedIn(): Boolean {
        val enabled = analyticsEnabledDs.get()!!
        return enabled
    }

    override suspend fun flush() {
        Log.i(TAG, "Firebase Analytics doesn't support flushing manually")
    }

    override suspend fun reset() {
        analytics.resetAnalyticsData()
    }

}