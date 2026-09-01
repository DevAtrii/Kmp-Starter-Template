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

/**
 * Stable identifier for an [StarterAnalyticsProvider].
 *
 * Built-in Mixpanel uses [StarterAnalyticsProviderIds.Mixpanel]. Custom / future
 * data modules (Firebase, …) pick their own unique value.
 */
data class StarterAnalyticsProviderId(
    val value: String,
)

object StarterAnalyticsProviderIds {
    val Mixpanel = StarterAnalyticsProviderId("mixpanel")
    val Firebase = StarterAnalyticsProviderId("firebase")
}
