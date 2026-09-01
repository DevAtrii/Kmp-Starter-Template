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

/** this is dummy analytics provider which logs the events */
class MockStarterStarterAnalyticsProvider : StarterAnalyticsProvider {
    private var isOpted = true
    private var userId: String? = null

    override val id: StarterAnalyticsProviderId
        get() = StarterAnalyticsProviderId("_starter_analytics")

    override suspend fun track(event: AppEvent) {
        track(
            event = event.event,
            properties = event.properties,
        )
    }

    override suspend fun track(event: String) {
        track(
            event = event,
            properties = null,
        )
    }

    override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) {
        track(
            event = event,
            properties = pair?.let { mapOf(it) },
        )
    }

    override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    ) {
        if (!isEnabled || !isOpted) return
        log("track event=$event properties=$properties")
    }

    override suspend fun setUserId(userId: String) {
        if (!isEnabled || !isOpted) return
        this.userId = userId
        log("setUserId=$userId")
    }

    override suspend fun optIn() {
        isOpted = true
        log("optIn")
    }

    override suspend fun optOut() {
        isOpted = false
        log("optOut")
    }

    override suspend fun toggleOptInOut() {
        if (isOpted) optOut() else optIn()
    }

    override suspend fun hasOptedIn(): Boolean = isOpted

    override suspend fun flush() {
        log("flush")
    }

    override suspend fun reset() {
        userId = null
        isOpted = true
        log("reset")
    }

    private fun log(message: String) {
        println("$TAG: $message")
    }

    companion object {
        private const val TAG = "StarterAnalyticsProvider"

        fun create() = MockStarterStarterAnalyticsProvider()
    }
}
