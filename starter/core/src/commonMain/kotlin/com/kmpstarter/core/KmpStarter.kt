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
    // keys
    val revenueCatApiKey: String,
    val mixPanelApiKey: String,
    // other
    val privacyPolicy: String = "",
    val termsOfUse: String = "",
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

    val PRIVACY_POLICY: String
        get() {
            if (!isInitialized)
                throw IllegalStateException("Please call KmpStarter.initApp(...) from app entry")
            return config.privacyPolicy
        }

    val TERMS_OF_USE: String
        get() {
            if (!isInitialized)
                throw IllegalStateException("Please call KmpStarter.initApp(...) from app entry")
            return config.termsOfUse
        }


    fun initApp(
        // keys
        revenueCatApiKey: String,
        mixPanelApiKey: String,
        privacyPolicy: String = "",
        termsOfUse: String = "",
    ) {
        synchronized(lock) {
            if (isInitialized)
                return
            config = KmpConfig(
                revenueCatApiKey = revenueCatApiKey,
                mixPanelApiKey = mixPanelApiKey,
                privacyPolicy = privacyPolicy,
                termsOfUse = termsOfUse,
            )
            isInitialized = true
        }
    }


}



















