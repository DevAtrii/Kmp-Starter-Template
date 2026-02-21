/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.feature_analytics_domain

/** contains all events related to current app*/
interface AppEventsTracker {

    // events names
    companion object {
        // onboarding
        const val KEY_ONBOARDING_TRAFFIC_SOURCE = "traffic_source"


    }

    suspend fun trackTrafficSource(source:String)
}



















