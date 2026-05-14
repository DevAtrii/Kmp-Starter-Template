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

@Serializable
data class RemoteAppMetadata(
    val versionName: String = "0.5.0",
    val versionCode: Int = 50,
)


abstract class RemoteConfigKey<T>(
    val key: String,
    open val defaultValue: T,
    val serializer: KSerializer<T>? = null,
)


/*Example of remote config keys*/
private sealed class DefaultKey<T>(
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






















