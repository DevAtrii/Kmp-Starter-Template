package com.kmpstarter.core.purchases.presentation.models

import androidx.compose.ui.graphics.vector.ImageVector

data class PurchaseFeature(
    val icon: ImageVector? = null,
    val label: String = "",
    val description: String? = null,
)
