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

package com.kmpstarter.feature_navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.kmpstarter.feature_navigation.screens.StarterScreens
import com.kmpstarter.feature_navigation.utils.rememberNavBackStack
import kotlinx.serialization.modules.subclass

@Composable
fun rememberStarterBackStack(): NavBackStack<NavKey> {
    val backstack = rememberNavBackStack(
        // replace initial screens here
        StarterScreens.Welcome
    ) {
        subclass(StarterScreens.Welcome::class)
        subclass(StarterScreens.Purchases::class)
        // add other screens here
    }
    return backstack
}