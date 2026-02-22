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

package com.kmpstarter.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


val lightScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

val darkScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )


@Composable
expect fun getDynamicColorScheme(darkTheme: Boolean): ColorScheme?



@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val dynamicColorScheme = getDynamicColorScheme(darkTheme = darkTheme)
    val colorScheme =
        when {
            dynamicColor && dynamicColorScheme != null -> dynamicColorScheme
            darkTheme -> darkScheme
            else -> lightScheme
        }

    val appTypography =
        Typography(
            displayLarge = baselineTypography.displayLarge.copy(
                fontFamily = getAppFontFamily()
            ),
            displayMedium = baselineTypography.displayMedium.copy(
                fontFamily = getAppFontFamily()
            ),
            displaySmall = baselineTypography.displaySmall.copy(
                fontFamily = getAppFontFamily()
            ),
            headlineLarge = baselineTypography.headlineLarge.copy(
                fontFamily = getAppFontFamily()
            ),
            headlineMedium = baselineTypography.headlineMedium.copy(
                fontFamily = getAppFontFamily()
            ),
            headlineSmall = baselineTypography.headlineSmall.copy(
                fontFamily = getAppFontFamily()
            ),
            titleLarge = baselineTypography.titleLarge.copy(
                fontFamily = getAppFontFamily()
            ),
            titleMedium = baselineTypography.titleMedium.copy(
                fontFamily = getAppFontFamily()
            ),
            titleSmall = baselineTypography.titleSmall.copy(
                fontFamily = getAppFontFamily()
            ),
            bodyLarge = baselineTypography.bodyLarge.copy(
                fontFamily = getAppFontFamily()
            ),
            bodyMedium = baselineTypography.bodyMedium.copy(
                fontFamily = getAppFontFamily()
            ),
            bodySmall = baselineTypography.bodySmall.copy(
                fontFamily = getAppFontFamily()
            ),
            labelLarge = baselineTypography.labelLarge.copy(
                fontFamily = getAppFontFamily()
            ),
            labelMedium = baselineTypography.labelMedium.copy(
                fontFamily = getAppFontFamily()
            ),
            labelSmall = baselineTypography.labelSmall.copy(
                fontFamily = getAppFontFamily()
            ),
        )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography,
        content = content,
    )


}
