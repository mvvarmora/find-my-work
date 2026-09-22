package com.example.findmywork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val LocalDarkTheme = androidx.compose.runtime.compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    primaryFixed = DarkPrimaryFixed,
    primaryFixedDim = DarkPrimaryFixedDim,
    onPrimaryFixed = DarkOnPrimaryFixed,
    onPrimaryFixedVariant = DarkOnPrimaryFixedVariant,
    inversePrimary = DarkInversePrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    secondaryFixed = DarkSecondaryFixed,
    secondaryFixedDim = DarkSecondaryFixedDim,
    onSecondaryFixed = DarkOnSecondaryFixed,
    onSecondaryFixedVariant = DarkOnSecondaryFixedVariant,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    tertiaryFixed = DarkTertiaryFixed,
    tertiaryFixedDim = DarkTertiaryFixedDim,
    onTertiaryFixed = DarkOnTertiaryFixed,
    onTertiaryFixedVariant = DarkOnTertiaryFixedVariant,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkFMWBackground,
    onBackground = DarkFMWTextPrimary,
    surface = DarkFMWSurface,
    onSurface = DarkFMWTextPrimary,
    surfaceVariant = DarkFMWSurfaceSubtle,
    onSurfaceVariant = DarkFMWTextSecondary,
    surfaceDim = DarkFMWBackground,
    surfaceBright = DarkFMWSurface,
    surfaceContainerLowest = Color(0xFF0F0F0F),
    surfaceContainerLow = Color(0xFF161616),
    surfaceContainer = DarkFMWSurface,
    surfaceContainerHigh = DarkFMWSurfaceSubtle,
    surfaceContainerHighest = Color(0xFF333333),
    surfaceTint = DarkPrimary,
    inverseSurface = LightFMWSurface,
    inverseOnSurface = LightFMWTextPrimary,
    outline = DarkFMWBorder,
    outlineVariant = DarkFMWDivider
)

private val LightColorScheme = lightColorScheme(
    primary = LightFMWNavy,
    onPrimary = Color.White,
    primaryContainer = LightFMWSoftBlue,
    onPrimaryContainer = LightFMWNavy,
    primaryFixed = LightFMWSoftBlue,
    primaryFixedDim = LightFMWSoftBlue,
    onPrimaryFixed = LightFMWNavy,
    onPrimaryFixedVariant = FMWNavyDeep,
    inversePrimary = FMWBlue,
    secondary = FMWOrange,
    onSecondary = Color.White,
    secondaryContainer = LightFMWAmberSoft,
    onSecondaryContainer = FMWOrange,
    secondaryFixed = LightFMWAmberSoft,
    secondaryFixedDim = LightFMWAmberSoft,
    onSecondaryFixed = LightFMWNavy,
    onSecondaryFixedVariant = FMWOrange,
    tertiary = FMWStarYellow,
    onTertiary = Color.White,
    tertiaryContainer = LightFMWWarningSoft,
    onTertiaryContainer = LightFMWNavy,
    tertiaryFixed = LightFMWWarningSoft,
    tertiaryFixedDim = LightFMWWarningSoft,
    onTertiaryFixed = LightFMWNavy,
    onTertiaryFixedVariant = FMWStarYellow,
    error = FMWError,
    onError = Color.White,
    errorContainer = LightFMWDangerSoft,
    onErrorContainer = FMWError,
    background = LightFMWBackground,
    onBackground = LightFMWTextPrimary,
    surface = LightFMWSurface,
    onSurface = LightFMWTextPrimary,
    surfaceVariant = LightFMWSurfaceSubtle,
    onSurfaceVariant = LightFMWTextSecondary,
    surfaceDim = LightFMWBackground,
    surfaceBright = LightFMWSurface,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = LightFMWBackground,
    surfaceContainer = LightFMWSurface,
    surfaceContainerHigh = LightFMWSurfaceSubtle,
    surfaceContainerHighest = LightFMWBackground,
    surfaceTint = LightFMWNavy,
    inverseSurface = LightFMWTextPrimary,
    inverseOnSurface = Color.White,
    outline = LightFMWBorder,
    outlineVariant = LightFMWDivider
)

@Composable
fun FindMyWorkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    androidx.compose.runtime.CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
