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

package com.kmpstarter.starter_features.analytics.domain.services

/** contains all events related to current app*/
interface AppEventsTracker {

    // events names
    companion object {
        // onboarding
        const val DUMMY_EVENT = "kmp_starter_event"


    }

    suspend fun dummyEvent(foo: String)
}



















