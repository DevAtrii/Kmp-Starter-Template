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

package com.kmpstarter.feature_locale

import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.StringResource


object StarterLocales {

    val DEFAULT = StarterLocale("🇺🇸", "en", LocaleRes.string.lang_en)

    private val _locales = mutableSetOf(DEFAULT)
    val locales get() = _locales.toSet()


    fun add(locale: StarterLocale) = _locales.add(locale)
    fun add(locales: Iterable<StarterLocale>) = _locales.addAll(locales)

    fun findBy(langCode: String): StarterLocale? {
        return _locales.find { it.langCode.equals(langCode, ignoreCase = true) }
    }

}

data class StarterLocale(
    val emoji: String,
    val langCode: String,
    val displayName: StringResource,
    val layoutDirection: LayoutDirection = LayoutDirection.Ltr,
)


/**
 * Supported locales for the app.
 *
 * langCode format:
 * - Language only: "en", "ar", "fr"
 * - Country-specific locale: use "_" between language and country
 *   Example: "es_AR" (Spanish - Argentina)
 */
/*
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
        */
/**
 * fallback
 * *//*

        val DEFAULT = ENGLISH

        */
/**
 * finds a locale by its language code
 *//*

        fun findBy(langCode: String): StarterLocales? {
            return entries.find { it.langCode.equals(langCode, ignoreCase = true) }
        }
    }


}*/
