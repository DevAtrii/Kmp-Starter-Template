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

package com.kmpstarter.feature_resources.locale

import androidx.compose.ui.unit.LayoutDirection
import com.kmpstarter.feature_resources.Res
import com.kmpstarter.feature_resources.lang_en
import com.kmpstarter.feature_resources.lang_es
import com.kmpstarter.feature_resources.lang_hi
import com.kmpstarter.feature_resources.lang_ur
import org.jetbrains.compose.resources.StringResource


/**
 * Supported locales for the app.
 *
 * langCode format:
 * - Language only: "en", "ar", "fr"
 * - Country-specific locale: use "_" between language and country
 *   Example: "es_AR" (Spanish - Argentina)
 */
enum class StarterLocales(
    val emoji: String,
    val langCode: String,
    val displayName: StringResource,
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
) {
    ENGLISH("🇺🇸", "en", Res.string.lang_en),
    URDU("🇵🇰", "ur", Res.string.lang_ur, LayoutDirection.Rtl),
    HINDI("🇮🇳", "hi", Res.string.lang_hi),
    SPANISH("🇪🇸", "es", Res.string.lang_es),
    // add more languages here
    // SPANISH_ARGENTINA("🇦🇷", "es_AR", Res.string.lang_es_AR)
    ;

    companion object {
        /**
         * fallback
         * */
        val DEFAULT = ENGLISH

        /**
         * finds a locale by its language code
         */
        fun findByLangCode(langCode: String): StarterLocales? {
            return entries.find { it.langCode.equals(langCode, ignoreCase = true) }
        }
    }


}