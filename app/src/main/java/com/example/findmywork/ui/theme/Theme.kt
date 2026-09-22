package com.example.findmywork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = compositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = FMWNavyDark,
    onPrimary = Color(0xFF002780),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFDCE1FF),
    secondary = FMWOrangeCTA,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E1C0C),
    onSecondaryContainer = Color(0xFFFFDDB3),
    tertiary = Color(0xFF6B9CFF),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF16253D),
    onTertiaryContainer = Color(0xFFDCE1FF),
    error = FMWDanger,
    onError = Color.White,
    errorContainer = Color(0xFF361416),
    onErrorContainer = Color(0xFFFFDAD6),
    background = FMWBgAppDark,
    onBackground = FMWTextPrimaryDark,
    surface = FMWBgCardDark,
    onSurface = FMWTextPrimaryDark,
    surfaceVariant = Color(0xFF232731),
    onSurfaceVariant = FMWTextSecondaryDark,
    surfaceDim = Color(0xFF121418),
    surfaceBright = Color(0xFF2E333F),
    surfaceContainerLowest = Color(0xFF0F1014),
    surfaceContainerLow = Color(0xFF161920),
    surfaceContainer = Color(0xFF1C1F26),
    surfaceContainerHigh = Color(0xFF232731),
    surfaceContainerHighest = Color(0xFF2B303C),
    surfaceTint = FMWNavyDark,
    inverseSurface = Color(0xFFE5E2E1),
    inverseOnSurface = Color(0xFF303030),
    outline = FMWBorderDark,
    outlineVariant = Color(0xFF3B4150)
)

private val LightColorScheme = lightColorScheme(
    primary = FMWNavyLight,
    onPrimary = Color.White,
    primaryContainer = FMWPrimaryLightLight,
    onPrimaryContainer = FMWNavyDeepLight,
    secondary = FMWOrangeCTA,
    onSecondary = Color.White,
    secondaryContainer = FMWOrangeSoftLight,
    onSecondaryContainer = FMWTextPrimaryLight,
    tertiary = FMWPrimaryBlueLight,
    onTertiary = Color.White,
    tertiaryContainer = FMWCategoryBlueLight,
    onTertiaryContainer = FMWNavyDeepLight,
    error = FMWDanger,
    onError = Color.White,
    errorContainer = FMWDangerSoftLight,
    onErrorContainer = FMWDanger,
    background = FMWBgAppLight,
    onBackground = FMWTextPrimaryLight,
    surface = FMWBgCardLight,
    onSurface = FMWTextPrimaryLight,
    surfaceVariant = FMWBgAppLight,
    onSurfaceVariant = FMWTextSecondaryLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = FMWBgAppLight,
    surfaceContainer = Color(0xFFF1F3F7),
    surfaceContainerHigh = Color(0xFFEAEFF5),
    surfaceContainerHighest = Color(0xFFE2E7F0),
    outline = FMWBorderLight,
    outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun FindMyWorkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
