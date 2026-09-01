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

import com.kmpstarter.feature_analytics_domain.StarterAnalyticsProvider
import com.kmpstarter.utils.datastore.AppDataStore
import com.kmpstarter.utils.logging.Log
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import org.koin.mp.KoinPlatform.getKoin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

object FirebaseAnalyticsScope {
    var enabled: Boolean = true
    var sessionTimeoutInterval: Duration = 30.minutes
    var defaultEventParameters: Map<String, String> = emptyMap()
    var userProperties: Map<String, String> = emptyMap()
    var adPersonalization: FirebaseAnalytics.ConsentStatus? = null
    var adStorage: FirebaseAnalytics.ConsentStatus? = null
    var adUserData: FirebaseAnalytics.ConsentStatus? = null
    var analyticsStorage: FirebaseAnalytics.ConsentStatus? = null

    private var initialized: Boolean = false

    internal fun isInitialized(): Boolean = initialized

    internal fun markInitialized() {
        initialized = true
    }
}

fun initFirebaseAnalytics(
    configure: FirebaseAnalyticsScope.() -> Unit = {},
) {
    if (FirebaseAnalyticsScope.isInitialized()) {
        Log.i("FirebaseAnalyticsScope", "already initialised")
        return
    }
    FirebaseAnalyticsScope.apply(configure)
    FirebaseAnalyticsScope.markInitialized()
}

internal fun FirebaseAnalyticsScope.get(): FirebaseAnalytics {
    val analytics = Firebase.analytics
    analytics.setAnalyticsCollectionEnabled(enabled)
    analytics.setSessionTimeoutInterval(sessionTimeoutInterval)
    if (defaultEventParameters.isNotEmpty()) {
        analytics.setDefaultEventParameters(defaultEventParameters)
    }
    userProperties.forEach { (name, value) ->
        analytics.setUserProperty(name = name, value = value)
    }
    val consent = buildMap {
        adPersonalization?.let {
            put(FirebaseAnalytics.ConsentType.AD_PERSONALIZATION, it)
        }
        adStorage?.let {
            put(FirebaseAnalytics.ConsentType.AD_STORAGE, it)
        }
        adUserData?.let {
            put(FirebaseAnalytics.ConsentType.AD_USER_DATA, it)
        }
        analyticsStorage?.let {
            put(FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE, it)
        }
    }
    if (consent.isNotEmpty()) {
        analytics.setConsent(consent)
    }
    return analytics
}

fun FirebaseAnalyticsScope.getProvider(): StarterAnalyticsProvider {
    val koin = getKoin()
    return FirebaseStarterAnalyticsProvider(
        analytics = koin.get(),
        appDataStore = koin.get<AppDataStore>(),
    )
}
