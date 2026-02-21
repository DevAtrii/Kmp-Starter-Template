package com.kmpstarter.feature_purchases_domain.models

import kotlinx.serialization.Serializable

/**
 * Configuration data used to dynamically populate the Paywall UI.
 * This allows for remote updates to marketing content without app releases.
 * @property faqs A set of frequently asked questions to reduce purchase friction.
 * @property reviews User testimonials used for social proof.
 * @property version The version of the paywall UI layout to be displayed.
 */
@Serializable
data class PaywallMetadata(
    val faqs: Set<Faq> = emptySet(),
    val reviews: Set<Review> = emptySet(),
    /**
     * The version of the paywall UI layout to be displayed.
     * * Use this to remotely control:
     * - **Layout Selection:** Map different integers to specific Compose layouts (e.g., `1` for Vertical, `2` for Horizontal).
     * - **A/B Testing:** Serve different version numbers to user segments to compare conversion rates.
     */
    val version: Int = 1,
)

/**
 * A customer testimonial to be displayed on the paywall.
 * * @property rating The star rating (standard range is 0.0 to 5.0).
 * @property review The text content of the user's feedback.
 * @property author The name of the reviewer.
 * @property profilePictureUrl Remote URL for the author's avatar.
 */
@Serializable
data class Review(
    val rating: Float = 5f,
    val review: String = "",
    val author: String = "",
    val profilePictureUrl: String = "",
)

/**
 * A common question and answer pair used to address user concerns.
 */
@Serializable
data class Faq(
    val question: String = "",
    val answer: String = "",
)