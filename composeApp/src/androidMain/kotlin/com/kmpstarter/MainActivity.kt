package com.kmpstarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kmpstarter.core.events.ThemeEvents
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.events.utils.ObserveAsEvents
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AndroidSideEffects()
            App()
        }
    }


}

@Composable
private fun AndroidSideEffects(
    themeEvents: ThemeEvents = koinInject(),
    systemUiController: SystemUiController = rememberSystemUiController(),
) {
    val currentThemeMode by themeEvents.themeMode.collectAsState(
        initial = ThemeEvents.DEFAULT_THEME_MODE
    )
    val isSystemInDarkTheme = isSystemInDarkTheme()
    ObserveAsEvents(
        flow = themeEvents.themeMode
    ) { themeMode ->
        val darkIcons = when (themeMode) {
            ThemeMode.LIGHT -> true
            ThemeMode.DARK -> false
            ThemeMode.SYSTEM -> !isSystemInDarkTheme
        }
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = darkIcons
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}



















