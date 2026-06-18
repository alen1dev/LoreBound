package com.pauls.lorebound.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════
// MINIMAL VOID — True black, white text, orange accent
// ═══════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = BrutalSurfaceVariant,
    onPrimaryContainer = Color.White,
    secondary = BrutalAccent,
    onSecondary = Color.Black,
    secondaryContainer = BrutalAccentDim.copy(alpha = 0.12f),
    onSecondaryContainer = BrutalAccent,
    tertiary = XpPurple,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF1A1000),
    onTertiaryContainer = XpPurple,
    background = Color.Black,
    onBackground = Color.White,
    surface = BrutalDarkSurface,
    onSurface = Color.White,
    surfaceVariant = BrutalSurfaceVariant,
    onSurfaceVariant = BrutalMuted,
    error = DangerRed,
    onError = Color.White,
    outline = BrutalSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF0F0F0),
    onPrimaryContainer = Color.Black,
    secondary = BrutalAccent,
    onSecondary = Color.White,
    tertiary = Color(0xFF7B1FA2),
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666)
)

@Composable
fun LoreBoundTheme(
    darkTheme: Boolean = true, // Ember Forge is always dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}