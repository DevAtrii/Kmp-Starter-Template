/*
 * Copyright (c) 2026 Athar Gul
 */

package com.kmpstarter.feature_remote_config_domain.logics

import com.kmpstarter.feature_remote_config_domain.RemoteConfig
import com.kmpstarter.feature_remote_config_domain.RemoteConfigKeys
import com.kmpstarter.feature_remote_config_domain.RemoteConfigRepository
import com.kmpstarter.feature_remote_config_domain._RemoteConfigInitializerState
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private typealias isInitializingFailed = Boolean

class GetConfigLogic(
    private val remoteConfigRepository: RemoteConfigRepository,
) {
    companion object {
        private const val TAG = "GetConfigLogic"
    }

    private val json = Json { ignoreUnknownKeys = true }

    @Suppress("UNCHECKED_CAST")
    private suspend fun <T : Any> getValue(key: RemoteConfigKeys<T>): T {
        val isInitializingFailed = waitForInitialization()
        if (isInitializingFailed) {
            Log.d(TAG, "invoke: initializingFailed returning default value")
            return key.defaultValue
        }

        val rawValue = remoteConfigRepository.get(key.key)
        Log.d(TAG, "invoke: ${key.key}=$rawValue")

        val result: Any = when (val defaultValue = key.defaultValue) {
            is Boolean -> rawValue.toBooleanStrictOrNull() ?: defaultValue
            is Int -> rawValue.toIntOrNull() ?: defaultValue
            is Long -> rawValue.toLongOrNull() ?: defaultValue
            is Double -> rawValue.toDoubleOrNull() ?: defaultValue
            is Float -> rawValue.toFloatOrNull() ?: defaultValue
            is String -> rawValue
            else -> {
                val serializer = key.serializer
                if (serializer == null) {
                    Log.w(TAG, "No serializer for ${key.key}. Returning default.")
                    defaultValue
                } else {
                    runCatching {
                        json.decodeFromString(serializer, rawValue)
                    }.getOrElse { e ->
                        Log.w(TAG, "Failed to decode JSON for ${key.key}: ${e.message}")
                        defaultValue
                    }
                }
            }
        }
        return result as T
    }

    @Suppress("UNCHECKED_CAST")
    suspend operator fun <T : Any> invoke(key: RemoteConfigKeys<T>): T {
        return getValue(key = key)
    }


    private suspend fun waitForInitialization(): isInitializingFailed {
        // Suspends until state is either Initialized or Failed
        val currentState = RemoteConfig.state.first { state ->
            state is _RemoteConfigInitializerState.Initialized ||
                    state is _RemoteConfigInitializerState.InitializationFailed
        }

        val isInitializingFailed =
            currentState is _RemoteConfigInitializerState.InitializationFailed

        if (isInitializingFailed) {
            Log.w(
                TAG,
                "Accessing config while Initialization failed. Values may be stale/defaults."
            )
        }

        return isInitializingFailed
    }
}