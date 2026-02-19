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

package com.kmpstarter.feature_remote_config_domain

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class RemoteAppMetadata(
    val versionName: String = "0.5.0",
    val versionCode: Int = 50,
)

sealed class RemoteConfigKeys<T>(
    val key: String,
    open val defaultValue: T,
    val serializer: KSerializer<T>? = null,
) {

    data class Metadata(
        override val defaultValue: RemoteAppMetadata = RemoteAppMetadata(),
    ) : RemoteConfigKeys<RemoteAppMetadata>(
        key = "meta_data",
        defaultValue = defaultValue,
        serializer = RemoteAppMetadata.serializer()
    )

    data class WelcomeText(
        override val defaultValue: String = "Welcome to KMP Starter",
    ) : RemoteConfigKeys<String>(
        key = "welcome_text",
        defaultValue = defaultValue
    )

    data class ShowOnboarding(
        override val defaultValue: Boolean = true,
    ) : RemoteConfigKeys<Boolean>(
        key = "show_onboarding",
        defaultValue = defaultValue
    )

    data class MinimumVersion(
        override val defaultValue: Int = 36,
    ) : RemoteConfigKeys<Int>(
        key = "minimum_version",
        defaultValue = defaultValue
    )
}



















