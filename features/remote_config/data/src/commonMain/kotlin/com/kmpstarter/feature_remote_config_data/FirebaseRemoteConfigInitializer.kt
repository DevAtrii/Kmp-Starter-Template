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

package com.kmpstarter.feature_remote_config_data

import com.kmpstarter.feature_remote_config_domain._RemoteConfigInitializer
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import kotlin.time.Duration

class _FirebaseRemoteConfigInitializer(
    private val remoteConfig: FirebaseRemoteConfig,
) : _RemoteConfigInitializer {



    companion object {
        private const val TAG = "_FirebaseRemoteConfigInitializer"
    }

    override suspend fun init(
        minimumFetchInterval: Duration,
        fetchTimeout: Duration,
        vararg defaults: Pair<String, Any?>,
    ): Boolean {
        remoteConfig.setDefaults(*defaults)
        remoteConfig.settings {
            this.minimumFetchInterval = minimumFetchInterval
            this.fetchTimeout = fetchTimeout
        }
        val activated = remoteConfig.fetchAndActivate()

        return activated
    }
}