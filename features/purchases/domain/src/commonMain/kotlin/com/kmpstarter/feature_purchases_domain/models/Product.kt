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

package com.kmpstarter.feature_purchases_domain.models

import kotlinx.serialization.Serializable

/**
 * A semantic alias for the unique identifier of a product (SKU) as defined in
 * the Google Play Console or Apple App Store.
 */
typealias ProductId = String

/**
 * A UI-ready representation of a store product.
 * * @property id The unique [ProductId] used for the purchase process.
 * @property title The localized name of the product (e.g., "Pro Monthly").
 * @property description A brief explanation of the product's benefits.
 * @property badge A visual highlight for the product (e.g., "Best Value").
 * @property price The formatted localized price string (e.g., "$9.99").
 * @property isTrial Whether this product includes a free trial period.
 * @property discountPercentage The calculated savings percentage compared to other plans.
 */
@Serializable
data class Product(
    val id: ProductId,
    val title: String,
    val description: String,
    val badge: ProductBadge,
    val price: String,
    val isTrial: Boolean,
    val discountPercentage: Int = 0,
) {
    fun formatValues(): Product {
        val title = this.title
        val description = this.description
        val badge = this.badge

        val newTitle = title.replace("%price%", this.price)
        val newDescription = description.replace("%price%", this.price)
        val newBadge = badge.copy(
            text = badge.text.replace("%price%", this.price)
        )

        return this.copy(
            title = newTitle,
            description = newDescription,
            badge = newBadge
        )
    }


}

/**
 * Visual metadata used to highlight specific products in the UI.
 * * @property text The label text (e.g., "POPULAR").
 * @property backgroundColor Hex code for the badge background (e.g., "#FF0000").
 */
@Serializable
data class ProductBadge(
    val text: String,
    val backgroundColor: String? = null,
)