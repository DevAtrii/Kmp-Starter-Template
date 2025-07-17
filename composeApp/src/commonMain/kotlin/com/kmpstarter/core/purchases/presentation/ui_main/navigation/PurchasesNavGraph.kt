package com.kmpstarter.core.purchases.presentation.ui_main.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.kmpstarter.core.purchases.presentation.ui_main.screens.PurchaseSubscriptionScreen
import com.kmpstarter.core.purchases.presentation.ui_main.screens.SamplePurchaseScreen
import com.kmpstarter.core.ui.composition_locals.LocalNavController
import com.kmpstarter.core.ui.utils.navigation.appNavComposable

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



















