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
 * Stable identifier for an [AnalyticsProvider].
 *
 * Built-in Mixpanel uses [AnalyticsProviderIds.Mixpanel]. Custom / future
 * data modules (Firebase, …) pick their own unique value.
 */
data class AnalyticsProviderId(
    val value: String,
)

object AnalyticsProviderIds {
    val Mixpanel = AnalyticsProviderId("mixpanel")
}
