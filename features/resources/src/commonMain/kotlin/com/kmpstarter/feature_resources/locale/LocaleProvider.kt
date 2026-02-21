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

package com.kmpstarter.feature_resources.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kmpstarter.ui_utils.datastore.rememberMutableDataStoreState
import com.kmpstarter.utils.logging.Log

//
/* viewmodel approach
internal class LocaleViewModel(
    private val appDataStore: AppDataStore,
    private val default: StarterLocales?,
) : ViewModel() {

    val dataStore = appDataStore.dataStore

    val languageCode = dataStore
        .data
        .map { prefs -> prefs[localeKey] }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            getDefaultLocale()
        )

    fun switchLanguage(languageCode: String) {
        viewModelScope.launch {
            dataStore.edit { mutablePrefs ->
                mutablePrefs[localeKey] = languageCode
            }
        }
    }

}
@Composable
fun rememberMutableStarterLocaleDataStore(default: StarterLocales?): MutableState<String?> {

    val vm: LocaleViewModel = koinViewModel {
        parametersOf(default)
    }
    val languageCode = vm.languageCode.collectAsState()
    return remember(languageCode) {
        object : MutableState<String?> {
            override var value: String?
                get() = languageCode.value
                set(value) {
                    if (value != null) {
                        vm.switchLanguage(value)
                    } else {
                        // remove
                    }
                }

            override fun component1(): String? = value

            override fun component2(): (String?) -> Unit = {
                value = it
            }
        }
    }
}
*/


internal val localeKey = stringPreferencesKey("app_locale")

@Composable
fun rememberMutableStarterLocaleDataStore(default: StarterLocales?): MutableState<String?> {
    val value = rememberMutableDataStoreState(
        key = localeKey,
        defaultValue = default?.langCode
    )

    return value
}


@Composable
fun rememberStarterLocaleDataStore(default: StarterLocales?): State<String?> {
    return rememberMutableStarterLocaleDataStore(default = default)
}

@Composable
fun rememberStarterActiveLocale(
    overrideDefault: StarterLocales? = null,
): StarterLocales {
    val localeState = rememberStarterLocaleDataStore(overrideDefault)

    // Priority is User Pref -> overrideDefault -> System Default
    val activeLocale = remember(localeState.value) {
        if (localeState.value != null) return@remember StarterLocales.findByLangCode(
            langCode = localeState.value!!
        ) ?: StarterLocales.DEFAULT


        val systemRaw = getDefaultLocale().replace("-", "_")

        // Find by full code, fallback to base language (e.g., en_US -> en)
        StarterLocales.findByLangCode(systemRaw)
            ?: StarterLocales.findByLangCode(systemRaw.substringBefore("_"))
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
    overrideDefault: StarterLocales? = null,
    content: @Composable () -> Unit,
) {
    // Priority is User Pref -> overrideDefault -> System Default
    val activeLocale = rememberStarterActiveLocale(overrideDefault = overrideDefault)

    CompositionLocalProvider(
        LocalAppLocale provides activeLocale.langCode,
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides activeLocale.layoutDirection,
        ) {
                content()
            // key(activeLocale) {
            //    content()
            // }
        }
    }
}

















