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

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AnalyticsRouterTest {

    @Test
    fun providerReturnsSpecificBackend() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))

        assertSame(mixpanel, analytics.provider(StarterAnalyticsProviderIds.Mixpanel))
        analytics.provider(FIREBASE).track("only_firebase")

        assertEquals(emptyList(), mixpanel.events)
        assertEquals(listOf("only_firebase"), firebase.events)
    }

    @Test
    fun combineFansOutToSelectedProvidersOnly() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val extra = FakeStarterAnalyticsProvider(EXTRA)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase, extra))

        analytics.combine(StarterAnalyticsProviderIds.Mixpanel, FIREBASE).track("combo")

        assertEquals(listOf("combo"), mixpanel.events)
        assertEquals(listOf("combo"), firebase.events)
        assertEquals(emptyList(), extra.events)
    }

    @Test
    fun combineSnapshotIgnoresLaterSwaps() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))
        val combined = analytics.combine(StarterAnalyticsProviderIds.Mixpanel, FIREBASE)

        analytics.setActiveProviders(FIREBASE)
        combined.track("fixed")

        assertEquals(listOf("fixed"), mixpanel.events)
        assertEquals(listOf("fixed"), firebase.events)
    }

    @Test
    fun setActiveProvidersReroutesExistingTrackerInstance() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))
        val tracker: EventsTracker = analytics

        tracker.track("both")
        analytics.setActiveProviders(FIREBASE)
        tracker.track("firebase_only")

        assertEquals(listOf("both"), mixpanel.events)
        assertEquals(listOf("both", "firebase_only"), firebase.events)
        assertEquals(listOf(FIREBASE), analytics.activeProviders)
    }

    @Test
    fun emptyActiveProvidersDisablesRoutedTracking() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val analytics = AnalyticsRouter.create(listOf(mixpanel))

        analytics.setActiveProviders()
        analytics.track("ignored")

        assertFalse(analytics.isEnabled)
        assertEquals(emptyList(), mixpanel.events)
        assertTrue(analytics.hasOptedIn())
    }

    @Test
    fun unknownAndDuplicateIdsFailFast() {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        assertFailsWith<IllegalArgumentException> {
            AnalyticsRouter.create(listOf(mixpanel, FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)))
        }

        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))
        assertFailsWith<IllegalArgumentException> {
            analytics.provider(EXTRA)
        }
        assertFailsWith<IllegalArgumentException> {
            analytics.combine(StarterAnalyticsProviderIds.Mixpanel, StarterAnalyticsProviderIds.Mixpanel)
        }
        assertFailsWith<IllegalArgumentException> {
            analytics.setActiveProviders(EXTRA)
        }
        assertFailsWith<IllegalArgumentException> {
            analytics.setActiveProviders(FIREBASE, FIREBASE)
        }
    }

    @Test
    fun fanOutStateMethodsHitActiveProviders() = runBlocking {
        val mixpanel = FakeStarterAnalyticsProvider(StarterAnalyticsProviderIds.Mixpanel)
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))

        analytics.setUserId("user-1")
        analytics.optOut()
        analytics.flush()
        analytics.reset()

        assertEquals(listOf("user-1"), mixpanel.userIds)
        assertEquals(listOf("user-1"), firebase.userIds)
        assertEquals(1, mixpanel.optOutCount)
        assertEquals(1, firebase.optOutCount)
        assertEquals(1, mixpanel.flushCount)
        assertEquals(1, firebase.flushCount)
        assertEquals(1, mixpanel.resetCount)
        assertEquals(1, firebase.resetCount)
        assertFalse(analytics.hasOptedIn())
    }

    @Test
    fun isEnabledIsTrueWhenAnyActiveProviderIsEnabled() {
        val mixpanel = FakeStarterAnalyticsProvider(
            id = StarterAnalyticsProviderIds.Mixpanel,
            isEnabled = false,
        )
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(mixpanel, firebase))

        assertTrue(analytics.isEnabled)
        analytics.setActiveProviders(StarterAnalyticsProviderIds.Mixpanel)
        assertFalse(analytics.isEnabled)
    }

    @Test
    fun nonCancellationFailureStillAttemptsRemainingProviders() = runBlocking {
        val failing = FakeStarterAnalyticsProvider(
            id = StarterAnalyticsProviderIds.Mixpanel,
            onTrack = { error("mixpanel down") },
        )
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(failing, firebase))

        val error = assertFailsWith<IllegalStateException> {
            analytics.track("event")
        }
        assertEquals("mixpanel down", error.message)
        assertEquals(listOf("event"), firebase.events)
    }

    @Test
    fun cancellationStopsRemainingProviders() = runBlocking {
        val cancelling = FakeStarterAnalyticsProvider(
            id = StarterAnalyticsProviderIds.Mixpanel,
            onTrack = { throw CancellationException("cancelled") },
        )
        val firebase = FakeStarterAnalyticsProvider(FIREBASE)
        val analytics = AnalyticsRouter.create(listOf(cancelling, firebase))

        assertFailsWith<CancellationException> {
            analytics.track("event")
        }
        assertEquals(emptyList(), firebase.events)
    }

    private companion object {
        val FIREBASE = StarterAnalyticsProviderId("firebase")
        val EXTRA = StarterAnalyticsProviderId("extra")
    }
}

private class FakeStarterAnalyticsProvider(
    override val id: StarterAnalyticsProviderId,
    override val isEnabled: Boolean = true,
    private val onTrack: (suspend (String) -> Unit)? = null,
) : StarterAnalyticsProvider {
    val events = mutableListOf<String>()
    val userIds = mutableListOf<String>()
    var optedIn: Boolean = true
    var optOutCount: Int = 0
    var flushCount: Int = 0
    var resetCount: Int = 0

    override suspend fun track(event: AppEvent) {
        track(event.event)
    }

    override suspend fun track(event: String) {
        onTrack?.invoke(event)
        events += event
    }

    override suspend fun track(event: String, pair: Pair<String, Any>?) {
        track(event)
    }

    override suspend fun track(event: String, properties: Map<String, Any>?) {
        track(event)
    }

    override suspend fun setUserId(userId: String) {
        userIds += userId
    }

    override suspend fun optIn() {
        optedIn = true
    }

    override suspend fun optOut() {
        optedIn = false
        optOutCount += 1
    }

    override suspend fun toggleOptInOut() {
        optedIn = !optedIn
    }

    override suspend fun hasOptedIn(): Boolean = optedIn

    override suspend fun flush() {
        flushCount += 1
    }

    override suspend fun reset() {
        resetCount += 1
    }

    override suspend fun setUserProperty(key: String, value: Any) = Unit

    override suspend fun setDefaultEventParameters(params: Map<String, Any>) = Unit
}
