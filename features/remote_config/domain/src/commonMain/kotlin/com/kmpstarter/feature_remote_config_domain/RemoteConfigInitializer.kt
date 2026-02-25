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

package com.kmpstarter.feature_remote_config_domain

import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.mp.KoinPlatform.getKoin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

sealed class _RemoteConfigInitializerState {
    object Idle : _RemoteConfigInitializerState()
    object Initializing : _RemoteConfigInitializerState()
    object Initialized : _RemoteConfigInitializerState()
    object InitializationFailed : _RemoteConfigInitializerState()
}

interface _RemoteConfigInitializer {


    @Throws(Exception::class)
    suspend fun init(
        minimumFetchInterval: Duration,
        fetchTimeout: Duration,
        vararg defaults: Pair<String, Any?>,
    ): Boolean
}


object RemoteConfig {

    private val _state =
        MutableStateFlow<_RemoteConfigInitializerState>(_RemoteConfigInitializerState.Idle)

    val state: StateFlow<_RemoteConfigInitializerState>
        get() = _state.asStateFlow()

    @Throws(Exception::class)
    suspend fun init(
        minimumFetchInterval: Duration = 1.hours,
        fetchTimeout: Duration = 2.minutes,
    ): Boolean {
        _state.emit(_RemoteConfigInitializerState.Initializing)
        val initializer: _RemoteConfigInitializer = getKoin().get()
        val initialized = runCatching {
            initializer.init(
                minimumFetchInterval = minimumFetchInterval,
                fetchTimeout = fetchTimeout,
            )
        }
            .onSuccess {
                _state.emit(_RemoteConfigInitializerState.Initialized)
                Log.d(
                    tag = null,
                    "Remote Config Initialized"
                )
            }
            .onFailure { e ->
                _state.emit(_RemoteConfigInitializerState.InitializationFailed)
                Log.e(
                    tag = null,
                    "failure initializing remote config ${e.message}"
                )
            }.getOrNull()

        return initialized ?: false
    }
}

