package com.kmpstarter

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.SnackbarResult.Dismissed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.kmpstarter.core.events.ThemeEvents
import com.kmpstarter.core.events.controllers.SnackbarController
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.ui.screens.WelcomeScreen
import com.kmpstarter.theme.ApplicationTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    GlobalSideEffects(snackbarHostState = snackbarHostState)
    MainApp(snackbarHostState = snackbarHostState)

}

@Composable
private fun MainApp(
    snackbarHostState: SnackbarHostState,
    themeEvents: ThemeEvents = koinInject(),
) {
    val currentThemeMode by themeEvents.themeMode.collectAsState(
        initial = ThemeMode.LIGHT
    )
    val currentDynamicColor by themeEvents.dynamicColor.collectAsState(
        initial = false
    )
    ApplicationTheme(
        darkTheme = currentThemeMode.toComposableBoolean(isSystemInDarkTheme()),
        dynamicColor = currentDynamicColor
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                )
            }
        ) { innerPaddings: PaddingValues ->
            WelcomeScreen(
                modifier = Modifier.padding(innerPaddings)
            )
        }

    }
}

@Composable
private fun GlobalSideEffects(snackbarHostState: SnackbarHostState) {
    LaunchedEffect(Unit) {
        // observing snackbar events & sending to the ui
        SnackbarController.events.collect {
            val result = snackbarHostState.showSnackbar(
                message = it.message,
                actionLabel = it.action?.name,
                duration = SnackbarDuration.Short
            )
            when (result) {
                Dismissed -> Unit
                ActionPerformed -> it.action?.action?.invoke()
            }
        }
    }
}



















