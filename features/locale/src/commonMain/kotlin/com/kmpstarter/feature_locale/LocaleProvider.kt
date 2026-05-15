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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kmpstarter.ui_utils.datastore.rememberMutableDataStoreState
import com.kmpstarter.utils.logging.Log


internal val localeKey = stringPreferencesKey("app_locale")

@Composable
fun rememberMutableStarterLocaleDataStore(default: StarterLocale?): MutableState<String?> {
    val value = rememberMutableDataStoreState(
        key = localeKey,
        defaultValue = default?.langCode
    )

    return value
}


@Composable
fun rememberStarterLocaleDataStore(default: StarterLocale?): State<String?> {
    return rememberMutableStarterLocaleDataStore(default = default)
}

@Composable
fun rememberStarterActiveLocale(
    overrideDefault: StarterLocale? = null,
): StarterLocale {
    val localeState = rememberStarterLocaleDataStore(overrideDefault)

    // Priority is User Pref -> overrideDefault -> System Default
    val activeLocale = remember(localeState.value) {
        if (localeState.value != null) {
            val language = StarterLocales.findBy(
                langCode = localeState.value!!
            )
            if (language != null)
                return@remember language
        }


        val systemRaw = getDefaultLocale().replace("-", "_")

        // Find by full code, fallback to base language (e.g., en_US -> en)
        StarterLocales.findBy(systemRaw)
            ?: StarterLocales.findBy(systemRaw.substringBefore("_"))
            ?: StarterLocales.DEFAULT // Ultimate fallback
    }

    LaunchedEffect(activeLocale) {
        Log.d(
            tag = null,
            "rememberStarterActiveLocale: activeLocale=${activeLocale.langCode}, localeState=${localeState.value}, default=${getDefaultLocale()}"
        )
    }

    return activeLocale
}

/**
 * Provides the application's locale context to the composition tree.
 * * **Resolution Priority:**
 * 1. **User Defined:** The locale manually selected by the user (persisted in DataStore/Settings).
 * 2. **Explicit Default:** The [overrideDefault] parameter provided to this function.
 * 3. **System Locale:** The current language setting of the physical device.
 *
 * @param overrideDefault An optional locale to use if no user preference is found.
 * @param content The composable UI that will consume the provided locale.
 */
@Composable
fun LocaleProvider(
    locales: Set<StarterLocale> = emptySet(),
    overrideDefault: StarterLocale? = null,
    content: @Composable () -> Unit,
) {
    // Priority is User Pref -> overrideDefault -> System Default
    val activeLocale = rememberStarterActiveLocale(overrideDefault = overrideDefault)

    LaunchedEffect(locales) {
        StarterLocales.add(locales)
    }

    CompositionLocalProvider(
        LocalAppLocale provides activeLocale.langCode,
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides activeLocale.layoutDirection,
        ) {
            content()
        }
    }
}

















