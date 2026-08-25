package com.example.impulseguard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OrganicColorScheme = lightColorScheme(
    primary = ColorAccent,
    onPrimary = ColorBg,
    secondary = ColorAccent2,
    onSecondary = ColorBg,
    tertiary = ColorAccent2,
    background = ColorBg,
    onBackground = ColorText,
    surface = ColorSurface,
    onSurface = ColorText,
    surfaceVariant = ColorSurface,
    onSurfaceVariant = ColorText,
    outline = ColorDivider,
    error = Accent700,
    onError = Color.White,
)

@Composable
fun ImpulseGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OrganicColorScheme,
        typography = Typography,
        content = content,
    )
}
