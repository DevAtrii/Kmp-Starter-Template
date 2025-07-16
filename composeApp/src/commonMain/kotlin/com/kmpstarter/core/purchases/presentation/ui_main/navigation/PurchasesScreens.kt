package com.kmpstarter.core.purchases.presentation.ui_main.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class PurchasesScreens {
    @Serializable
    data object Root : PurchasesScreens()

    @Serializable
    data object SubscriptionScreen : PurchasesScreens()


}