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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

/**
 * Base contract for all Remote Config keys.
 *
 * Each implementation represents single remote config entry.
 *
 * Example generated keys:
 * - "meta_data"
 * - "welcome_text"
 * - "show_onboarding"
 * - "minimum_version"
 *
 * See [ConfigKeys]:
 * - [ConfigKeys.Metadata]
 * - [ConfigKeys.WelcomeText]
 * - [ConfigKeys.ShowOnboarding]
 * - [ConfigKeys.MinimumVersion]
 *
 * @param key Unique remote config key stored in provider.
 * @param defaultValue Fallback value used when remote value unavailable.
 * @param serializer Optional serializer for complex objects.
 */
abstract class RemoteConfigKey<T>(
    val key: String,
    open val defaultValue: T,
    val serializer: KSerializer<T>? = null,
)


/*Example of remote config keys*/


@Serializable
private data class RemoteAppMetadata(
    val versionName: String = "0.5.0",
    val versionCode: Int = 50,
)


private sealed class ConfigKeys<T>(
    key: String,
    defaultValue: T,
    serializer: KSerializer<T>? = null,
) : RemoteConfigKey<T>(
    key = key,
    defaultValue = defaultValue,
    serializer = serializer
) {
    data class Metadata(
        override val defaultValue: RemoteAppMetadata = RemoteAppMetadata(),
    ) : RemoteConfigKey<RemoteAppMetadata>(
        key = "meta_data",
        defaultValue = defaultValue,
        serializer = RemoteAppMetadata.serializer()
    )

    data class WelcomeText(
        override val defaultValue: String = "Welcome to KMP Starter",
    ) : RemoteConfigKey<String>(
        key = "welcome_text",
        defaultValue = defaultValue
    )

    data class ShowOnboarding(
        override val defaultValue: Boolean = true,
    ) : RemoteConfigKey<Boolean>(
        key = "show_onboarding",
        defaultValue = defaultValue
    )

    data class MinimumVersion(
        override val defaultValue: Int = 36,
    ) : RemoteConfigKey<Int>(
        key = "minimum_version",
        defaultValue = defaultValue
    )
}






















