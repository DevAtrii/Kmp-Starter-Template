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

package com.kmpstarter.feature_remote_config_presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.kmpstarter.feature_remote_config_domain.RemoteConfigKeys
import com.kmpstarter.feature_remote_config_domain.logics.GetConfigLogic
import org.koin.compose.koinInject

@Composable
fun <T : Any> rememberRemoteConfig(key: RemoteConfigKeys<T>): State<T> {
    val getConfig: GetConfigLogic = koinInject()
    return produceState(initialValue = key.defaultValue, key1 = key) {
        value = getConfig(key = key)
    }
}


















