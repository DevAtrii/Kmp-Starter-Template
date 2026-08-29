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

package com.kmpstarterapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.SnackbarResult.Dismissed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.NavKey
import com.kmpstarter.core.datastore.theme.ThemeDataStore
import com.kmpstarter.core.events.controllers.SnackbarController
import com.kmpstarter.core.platform.ifAndroid
import com.kmpstarter.core.platform.platform
import com.kmpstarter.feature_locale.LocaleProvider
import com.kmpstarter.feature_locale.StarterLocale
import com.kmpstarter.feature_locale.StarterLocales
import com.kmpstarter.feature_navigation.StarterNavigation
import com.kmpstarter.feature_resources.Res
import com.kmpstarter.feature_resources.lang_es
import com.kmpstarter.feature_resources.lang_hi
import com.kmpstarter.feature_resources.lang_ur
import com.kmpstarter.ui_utils.composition_locals.LocalThemeMode
import com.kmpstarter.ui_utils.side_effects.LaunchOnce
import com.kmpstarter.ui_utils.side_effects.ObserveAsEvents
import com.kmpstarter.ui_utils.store.AppUpdateProvider
import com.kmpstarter.utils.logging.Log
import com.kmpstarter.utils.starter.ExperimentalStarterApi
import com.kmpstarterapp.core.KmpAppInitializer
import com.kmpstarterapp.core.navigation.AppScreens
import com.kmpstarterapp.theme.ApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.koinInject

/**
 * App level static configuration.
 *
 * Contains:
 * - feature flags
 * - supported locales
 * - navigation serialization setup
 */
private object AppConfig {

    /**
     * Enables force update flow.
     *
     * When `true`, app can block usage until
     * minimum required version installed.
     */
    const val FORCE_UPDATE = true

    /**
     * List of supported app locales.
     *
     * Used by locale picker and localization system.
     */
    val supportedLocales = setOf(
        StarterLocale(
            "🇵🇰",
            "ur",
            Res.string.lang_ur,
            LayoutDirection.Rtl
        ),
        StarterLocale(
            "🇮🇳",
            "hi",
            Res.string.lang_hi
        ),
        StarterLocale(
            "🇪🇸",
            "es",
            Res.string.lang_es
        ),
    )

    /**
     * Registers all navigation destinations
     * for polymorphic serialization.
     *
     * Required for navigation state restoration
     * and screen serialization.
     */
    val navigationPolymorphicBuilder: PolymorphicModuleBuilder<NavKey>.() -> Unit = {
        subclass(AppScreens.Welcome::class)
        subclass(AppScreens.Splash::class)
        subclass(AppScreens.Onboarding::class)
        subclass(AppScreens.Purchases::class)
    }

    /*
    // Alternative locale registration approach.
    // Can register globally instead of passing
    // locales to LocaleProvider.

    init {
        StarterLocales.add(supportedLocales)
    }
    */
}


/**
 * The main entry point of the application UI.
 */
@OptIn(ExperimentalStarterApi::class)
@Composable
fun App() {
    val appInitializer: KmpAppInitializer = koinInject()
    LaunchOnce {
        appInitializer.initialize()
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    GlobalSideEffects(snackbarHostState = snackbarHostState)
    MainApp(snackbarHostState = snackbarHostState)


}

/**
 * The core UI layout.
 * Configures the App Update logic, Localization, Theme preferences,
 * and hosts the main Navigation graph.
 */
@OptIn(ExperimentalStarterApi::class)
@Composable
private fun MainApp(
    snackbarHostState: SnackbarHostState,
    themeDataStore: ThemeDataStore = koinInject(),
) {
    val currentThemeMode by themeDataStore.themeMode.collectAsState(
        initial = ThemeDataStore.DEFAULT_THEME_MODE
    )
    val currentDynamicColor by themeDataStore.dynamicColor.collectAsState(
        initial = ThemeDataStore.DEFAULT_DYNAMIC_COLOR_SCHEME
    )

    AppUpdateProvider(
        force = AppConfig.FORCE_UPDATE
    ) {
        LocaleProvider(
            locales = AppConfig.supportedLocales,
            overrideDefault = StarterLocales.findBy("en"),
        ) {
            CompositionLocalProvider(LocalThemeMode provides currentThemeMode) {
                ApplicationTheme(
                    darkTheme = currentThemeMode.isInDarkTheme(isSystemInDarkTheme()),
                    dynamicColor = currentDynamicColor
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState
                            )
                        }
                    ) { _: PaddingValues ->
                        StarterNavigation(
                            AppScreens.Splash,
                            builderAction = AppConfig.navigationPolymorphicBuilder,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

/**
 * Handles non-UI logic triggered by events.
 * Listens for global snackbar requests and manages their display and dismissal.
 */
@Composable
private fun GlobalSideEffects(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    ObserveAsEvents(
        flow = SnackbarController.events,
    ) { snackbarEvent ->
        // launching another scope inside launched effect for ui of snackbar
        scope.launch {
            if (snackbarEvent.dismissPrevious && snackbarHostState.currentSnackbarData != null) {
                snackbarHostState.currentSnackbarData?.dismiss()
                delay(100)
            }

            if (snackbarEvent.message.isEmpty())
                return@launch

            val result = snackbarHostState.showSnackbar(
                message = snackbarEvent.message,
                actionLabel = snackbarEvent.action?.name,
                duration = SnackbarDuration.Short
            )
            when (result) {
                Dismissed -> Unit
                ActionPerformed -> {
                    snackbarEvent.action?.action?.invoke()
                }
            }
        }
    }
}