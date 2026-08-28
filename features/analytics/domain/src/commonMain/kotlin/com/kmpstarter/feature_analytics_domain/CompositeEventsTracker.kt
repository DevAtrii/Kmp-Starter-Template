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

import kotlin.coroutines.cancellation.CancellationException

internal class CompositeEventsTracker(
    private val providers: () -> List<AnalyticsProvider>,
) : EventsTracker {

    override val isEnabled: Boolean
        get() = currentProviders().any { it.isEnabled }

    override suspend fun track(event: AppEvent) {
        val current = currentProviders()
        if (current.none { it.isEnabled }) return
        forEachProvider(current) { it.track(event) }
    }

    override suspend fun track(event: String) {
        val current = currentProviders()
        if (current.none { it.isEnabled }) return
        forEachProvider(current) { it.track(event) }
    }

    override suspend fun track(
        event: String,
        pair: Pair<String, Any>?,
    ) {
        val current = currentProviders()
        if (current.none { it.isEnabled }) return
        forEachProvider(current) { it.track(event, pair) }
    }

    override suspend fun track(
        event: String,
        properties: Map<String, Any>?,
    ) {
        val current = currentProviders()
        if (current.none { it.isEnabled }) return
        forEachProvider(current) { it.track(event, properties) }
    }

    override suspend fun setUserId(userId: String) {
        val current = currentProviders()
        if (current.none { it.isEnabled }) return
        forEachProvider(current) { it.setUserId(userId) }
    }

    override suspend fun optIn() {
        forEachProvider(currentProviders()) { it.optIn() }
    }

    override suspend fun optOut() {
        forEachProvider(currentProviders()) { it.optOut() }
    }

    override suspend fun toggleOptInOut() {
        forEachProvider(currentProviders()) { it.toggleOptInOut() }
    }

    override suspend fun hasOptedIn(): Boolean {
        val current = currentProviders()
        var firstError: Throwable? = null
        var allOptedIn = true
        for (provider in current) {
            try {
                if (!provider.hasOptedIn()) {
                    allOptedIn = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                if (firstError == null) firstError = e
            }
        }
        firstError?.let { throw it }
        return allOptedIn
    }

    override suspend fun flush() {
        forEachProvider(currentProviders()) { it.flush() }
    }

    override suspend fun reset() {
        forEachProvider(currentProviders()) { it.reset() }
    }

    private fun currentProviders(): List<AnalyticsProvider> = providers()

    private suspend inline fun forEachProvider(
        current: List<AnalyticsProvider>,
        action: suspend (AnalyticsProvider) -> Unit,
    ) {
        var firstError: Throwable? = null
        for (provider in current) {
            try {
                action(provider)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                if (firstError == null) firstError = e
            }
        }
        firstError?.let { throw it }
    }
}
