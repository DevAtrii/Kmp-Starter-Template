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

package com.kmpstarter.core.events.navigator.utils

import androidx.navigation.NavHostController
import com.kmpstarter.core.events.navigator.interfaces.NavigationAction

fun NavHostController.handleNavigationAction(action: NavigationAction) {
    when (action) {
        is NavigationAction.Navigate ->
            navigate(action.destination) {
                apply(action.navOptions)
            }

        NavigationAction.NavigateUp -> navigateUp()
    }
}
