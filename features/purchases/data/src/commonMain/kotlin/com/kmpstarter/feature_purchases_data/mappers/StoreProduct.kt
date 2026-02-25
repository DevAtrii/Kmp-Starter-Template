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

package com.kmpstarter.feature_purchases_data.mappers

import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.models.ProductBadge
import com.revenuecat.purchases.kmp.models.StoreProduct

internal fun StoreProduct.toProduct(
    title: String,
    description: String,
    badge: String,
    badgeBg: String?,
    isTrial: Boolean,
    discountPercentage: Int,
): Product = Product(
    id = this.id,
    title = title,
    description = description,
    badge = ProductBadge(
        text = badge,
        backgroundColor = badgeBg
    ),
    price = this.price.formatted,
    isTrial = isTrial,
    discountPercentage = discountPercentage
)

internal fun StoreProduct.toProduct(
    title: String,
    description: String,
    badge: String,
    badgeBg: String?,
    isTrial: Boolean,
): Product = toProduct(
    title = title,
    description = description,
    badge = badge,
    badgeBg = badgeBg,
    isTrial = isTrial,
    discountPercentage = 0
)
