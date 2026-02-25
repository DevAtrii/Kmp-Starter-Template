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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {

    private var defaultLocale: Locale? = null

    actual val current: String
        @Composable
        get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if(defaultLocale == null) {
            defaultLocale = Locale.getDefault()
        }

        val newLocale = if(value == null) {
            defaultLocale!!
        } else {
            Locale.forLanguageTag(value)
        }

        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)

        val context = LocalContext.current
        val newContext = context.createConfigurationContext(configuration)

        return LocalContext provides newContext
    }
}