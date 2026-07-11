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

package com.kmpstarter.feature_locale.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kmpstarter.feature_locale.StarterLocale
import com.kmpstarter.feature_locale.StarterLocales
import com.kmpstarter.feature_locale.rememberMutableStarterLocaleDataStore

data class LocaleSelectorArgs(
    val currentLocale: StarterLocale,
    val onLocaleSelected: (StarterLocale) -> Unit,
)

@Composable
fun LocaleSelectorContainer(
    content: @Composable (args: LocaleSelectorArgs) -> Unit,
) {
    var _localeLangCode by  rememberMutableStarterLocaleDataStore(
        default = null
    )
    val args = remember(_localeLangCode) {
        LocaleSelectorArgs(
            currentLocale = _localeLangCode?.let {
                StarterLocales.findBy(
                    it
                )
            }
                ?: StarterLocales.DEFAULT,
            onLocaleSelected = { locale ->
                _localeLangCode = locale.langCode
            }
        )
    }
    content(args)
}