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

package com.kmpstarter.feature_purchases.presentation.ui_main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.kmpstarter.feature_purchases.presentation.ui_main.screens.SamplePurchaseScreen
import com.kmpstarter.ui_utils.composition_locals.LocalNavController
import com.kmpstarter.ui_utils.navigation.appNavComposable

fun NavGraphBuilder.purchasesNavGraph(
    scaffoldModifier: Modifier,
) {
    navigation<PurchasesScreens.Root>(
        startDestination = PurchasesScreens.SubscriptionScreen
    ) {
        appNavComposable<PurchasesScreens.SubscriptionScreen> {
            /*Todo Remove Dummy screen & uncomment the actual screen*/
            // for testing added dummy screen
            val navController = LocalNavController.current
             SamplePurchaseScreen(
                modifier = scaffoldModifier,
                onDismiss = {
                    navController.navigateUp()
                }
            )

            /*PurchaseSubscriptionScreen(
                modifier = scaffoldModifier,
                features = listOf(),
                subscriptionTerms = listOf(),
                onDismiss = {
                    navController.navigateUp()
                }
            )*/
        }
    }
}



















