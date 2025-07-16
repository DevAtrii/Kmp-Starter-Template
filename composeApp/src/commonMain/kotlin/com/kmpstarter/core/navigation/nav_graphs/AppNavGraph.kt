package com.kmpstarter.core.navigation.nav_graphs

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import com.kmpstarter.core.purchases.presentation.ui_main.navigation.purchasesNavGraph
import com.kmpstarter.starter_features.auth.presentation.ui_main.navigation.authNavGraph

fun NavGraphBuilder.appNavGraph(
    scaffoldModifier: Modifier,
) {
    authNavGraph(
        scaffoldModifier = scaffoldModifier
    )
    starterNavGraph(
        scaffoldModifier = scaffoldModifier
    )
    purchasesNavGraph(
        scaffoldModifier = scaffoldModifier
    )
    /*Todo add other nav graphs here*/
}
