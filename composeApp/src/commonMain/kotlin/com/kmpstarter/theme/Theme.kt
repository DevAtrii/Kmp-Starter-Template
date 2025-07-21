package com.kmpstarter.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color


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

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme =
    ColorFamily(
        Color.Unspecified,
        Color.Unspecified,
        Color.Unspecified,
        Color.Unspecified,
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
                fontFamily = getPoppinsFontFamily()
            ),
            displayMedium = baselineTypography.displayMedium.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            displaySmall = baselineTypography.displaySmall.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            headlineLarge = baselineTypography.headlineLarge.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            headlineMedium = baselineTypography.headlineMedium.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            headlineSmall = baselineTypography.headlineSmall.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            titleLarge = baselineTypography.titleLarge.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            titleMedium = baselineTypography.titleMedium.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            titleSmall = baselineTypography.titleSmall.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            bodyLarge = baselineTypography.bodyLarge.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            bodyMedium = baselineTypography.bodyMedium.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            bodySmall = baselineTypography.bodySmall.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            labelLarge = baselineTypography.labelLarge.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            labelMedium = baselineTypography.labelMedium.copy(
                fontFamily = getPoppinsFontFamily()
            ),
            labelSmall = baselineTypography.labelSmall.copy(
                fontFamily = getPoppinsFontFamily()
            ),
        )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography,
        content = content,
    )


}
