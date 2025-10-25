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

import kotlinx.serialization.Serializable

@Serializable
sealed class PurchasesScreens {
    @Serializable
    data object Root : PurchasesScreens()

    @Serializable
    data object SubscriptionScreen : PurchasesScreens()


}