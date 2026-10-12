package com.kris.aurelvio.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AurelvioColors(
    val gold: Color,
    val goldSoft: Color,
    val positive: Color,
    val negative: Color,
    val mutedText: Color,
    val surfaceElevated: Color,
    val chartLine: Color,
    val chartFillTop: Color,
    val chartFillBottom: Color
)

val DarkAurelvioColors = AurelvioColors(
    gold = Color(0xFFD4AF37),
    goldSoft = Color(0xFFF0D77B),
    positive = Color(0xFF3DDC97),
    negative = Color(0xFFFF6B6B),
    mutedText = Color(0xFF8B93A8),
    surfaceElevated = Color(0xFF1B2238),
    chartLine = Color(0xFFD4AF37),
    chartFillTop = Color(0x4DD4AF37),
    chartFillBottom = Color(0x00D4AF37)
)

val LightAurelvioColors = AurelvioColors(
    gold = Color(0xFF9A7B1C),
    goldSoft = Color(0xFFC4A038),
    positive = Color(0xFF138A5B),
    negative = Color(0xFFC94545),
    mutedText = Color(0xFF5C6378),
    surfaceElevated = Color(0xFFEFECE6),
    chartLine = Color(0xFF9A7B1C),
    chartFillTop = Color(0x4D9A7B1C),
    chartFillBottom = Color(0x009A7B1C)
)

val LocalAurelvioColors = staticCompositionLocalOf { DarkAurelvioColors }
