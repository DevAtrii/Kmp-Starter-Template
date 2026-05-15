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

package com.kmpstarter.feature_purchases_presentation.ui.screens.paywalls

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class PaywallV1UiContent(
    val heroImage: DrawableResource,
    val title: StringResource,
    val productsNotFoundTitle: StringResource,
    val tryAgainButtonText: StringResource,
    val purchasingText: StringResource,
    val trialButtonText: StringResource,
    val continueButtonText: StringResource,
    val restorePurchasesText: StringResource,
    val privacyPolicyText: StringResource,
    val termsOfUseText: StringResource,
    val reviewsTitle: StringResource,
    val faqsTitle: StringResource,
    val features: List<PurchaseFeature>,
)
