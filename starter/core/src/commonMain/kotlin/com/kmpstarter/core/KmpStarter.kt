/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

package com.kmpstarter.core

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


data class KmpConfig(
    val isDebug: Boolean,
    // keys
    val revenueCatApiKey: String,
    val mixPanelApiKey: String,
)

object KmpStarter {

    private var isInitialized = false

    private lateinit var config: KmpConfig
    private val lock = SynchronizedObject()

    val REVENUE_CAT_API_KEY: String
        get() {
            if (!isInitialized)
                throw IllegalStateException("Please call KmpStarter.initApp(...) from app entry")
            return config.revenueCatApiKey
        }

    val MIXPANEL_API_KEY: String
        get() {
            if (!isInitialized)
                throw IllegalStateException("Please call KmpStarter.initApp(...) from app entry")
            return config.mixPanelApiKey
        }

    val IS_DEBUG: Boolean
        get() {
            if (!isInitialized)
                throw IllegalStateException("Please call KmpStarter.initApp(...) from app entry")
            return config.isDebug
        }


    fun initApp(
        isDebug: Boolean,
        // keys
        revenueCatApiKey: String,
        mixPanelApiKey: String,
    ) {
        synchronized(lock) {
            if (isInitialized)
                return
            config = KmpConfig(
                isDebug = isDebug,
                revenueCatApiKey = revenueCatApiKey,
                mixPanelApiKey = mixPanelApiKey
            )
            isInitialized = true
        }
    }


}



















