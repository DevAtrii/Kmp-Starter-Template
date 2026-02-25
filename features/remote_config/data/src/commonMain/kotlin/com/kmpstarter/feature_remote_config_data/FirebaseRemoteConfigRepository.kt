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

import com.kmpstarter.feature_remote_config_domain.RemoteConfigRepository
import com.kmpstarter.feature_remote_config_domain.RemoteConfigValue
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get

class FirebaseRemoteConfigRepository(
    private val remoteConfig: FirebaseRemoteConfig,
) : RemoteConfigRepository {


    override fun get(key: String): RemoteConfigValue {
        val value = remoteConfig.get<RemoteConfigValue>(
            key = key
        )
        return value
    }


}