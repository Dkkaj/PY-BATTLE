package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Playt24DarkColorScheme = darkColorScheme(
    primary = PlaytCrimson,
    onPrimary = PlaytTextPrimary,
    primaryContainer = PlaytSurfaceElevated,
    onPrimaryContainer = PlaytTextPrimary,
    secondary = PlaytOrange,
    onSecondary = PlaytTextPrimary,
    secondaryContainer = PlaytSurfaceElevated,
    onSecondaryContainer = PlaytCoinGold,
    tertiary = PlaytElectricBlue,
    background = PlaytBgObsidian,
    onBackground = PlaytTextPrimary,
    surface = PlaytSurfaceDark,
    onSurface = PlaytTextPrimary,
    surfaceVariant = PlaytSurfaceElevated,
    onSurfaceVariant = PlaytTextSecondary,
    error = PlaytDanger,
    outline = PlaytCardBorder
)

@Composable
fun Playt24Theme(
    content: @Composable () -> Unit
) {
    // Unified dark gaming theme across every screen (no white/dark mixing)
    MaterialTheme(
        colorScheme = Playt24DarkColorScheme,
        typography = Typography,
        content = content
    )
}
