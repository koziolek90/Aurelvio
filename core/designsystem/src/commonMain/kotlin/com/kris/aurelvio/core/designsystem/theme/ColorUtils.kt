package com.kris.aurelvio.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Parses a hex color string (e.g. "#F59E0B" or "F59E0B") into Compose [Color].
 * Returns [fallback] if parsing fails.
 */
fun parseHexColor(hex: String, fallback: Color = Color.Unspecified): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(colorInt or 0xFF000000L)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        fallback
    }
}
