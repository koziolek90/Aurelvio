package com.kris.aurelvio.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD4AF37),
    onPrimary = Color(0xFF0A0E1A),
    primaryContainer = Color(0xFF1B2238),
    onPrimaryContainer = Color(0xFFF0D77B),
    secondary = Color(0xFFF0D77B),
    onSecondary = Color(0xFF0A0E1A),
    background = Color(0xFF0A0E1A),
    onBackground = Color(0xFFEAEDF5),
    surface = Color(0xFF121829),
    onSurface = Color(0xFFEAEDF5),
    surfaceVariant = Color(0xFF1B2238),
    onSurfaceVariant = Color(0xFF8B93A8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF9A7B1C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEFECE6),
    onPrimaryContainer = Color(0xFF12162A),
    secondary = Color(0xFFC4A038),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F6F2),
    onBackground = Color(0xFF12162A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF12162A),
    surfaceVariant = Color(0xFFEFECE6),
    onSurfaceVariant = Color(0xFF5C6378)
)

@Composable
fun AurelvioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkAurelvioColors else LightAurelvioColors

    CompositionLocalProvider(
        LocalAurelvioColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

object AurelvioTheme {
    val colors: AurelvioColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAurelvioColors.current

    val spacing: AurelvioSpacing
        get() = AurelvioSpacing
}
