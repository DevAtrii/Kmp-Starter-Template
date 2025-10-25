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

package com.kmpstarter.core.navigation.nav_graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import com.kmpstarter.feature_purchases.presentation.ui_main.navigation.purchasesNavGraph
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.appNavGraph(
    scaffoldModifier: Modifier,
    navigator: Navigator,
    scope: CoroutineScope,
) {
    starterNavGraph(
        scaffoldModifier = scaffoldModifier
    )
    purchasesNavGraph(
        scaffoldModifier = scaffoldModifier
    )
    /*Todo add other nav graphs here*/
}
