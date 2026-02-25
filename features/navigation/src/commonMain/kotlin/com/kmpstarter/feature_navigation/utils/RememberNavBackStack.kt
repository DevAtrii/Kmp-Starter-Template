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

package com.kmpstarter.feature_navigation.utils

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


@OptIn(InternalSerializationApi::class)
@Composable
inline fun rememberNavBackStack(
    vararg elements: NavKey,
    crossinline builderAction: PolymorphicModuleBuilder<NavKey>.() -> Unit,
): NavBackStack<NavKey> = rememberNavBackStack(
    configuration = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(
                NavKey::class,
                builderAction = builderAction
            )
        }
    },
    elements = elements
)



