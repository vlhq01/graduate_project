package com.example.graduateproject.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Solid Accents
val AccentOrange = Color(0xFFFF7A52)
val AccentGreen = Color(0xFF56D6A0)
val AccentTeal = Color(0xFF7ECBB8)
val AccentPeach = Color(0xFFFFB088)
val AccentLightGreen = Color(0xFF8CE8C4)
val RatingStar = Color(0xFFFFB347)

// Legacy for backward compatibility (Will be replaced)
val PeachSolid = AccentOrange
val MintSolid = AccentGreen
val MintLight = Color(0xFFB8F0D8).copy(alpha = 0.95f)
val PeachLight = Color(0xFFFFD6C4).copy(alpha = 0.95f)

// Light Theme Colors (Derived from Figma App.tsx)
val LightBackground = Color(0xFFFAFAFB)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceElevated = Color(0xFFFFFFFF)
val LightSurfaceVariant = LightSurfaceElevated
val LightInputBackground = Color(0xFFF3F3F5)
val LightOnBackground = Color(0xFF0A0A0A)
val LightOnSurfaceVariant = Color(0xFF717182)
val LightOutline = Color(0x1A000000) // rgba(0,0,0,0.10)

// Dark Theme Colors (Derived from Figma App.tsx)
val DarkBackground = Color(0xFF0D0D10)
val DarkSurface = Color(0xFF18181C)
val DarkSurfaceElevated = Color(0xFF222228)
val DarkSurfaceVariant = DarkSurfaceElevated
val DarkInputBackground = Color(0xFF1E1E24)
val DarkOnBackground = Color(0xFFF0F0F3)
val DarkOnSurfaceVariant = Color(0xFF6B6B7A)
val DarkOutline = Color(0x12FFFFFF) // rgba(255,255,255,0.07) -> 0.07 * 255 = 17.85 -> 0x12

// Gradients (Derived from Figma App.tsx)
val LightHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xE0B8F0D8),
        Color(0xE0FFD6C4)
    ) // rgba(184,240,216,0.88) -> rgba(255,214,196,0.88)
)
val DarkHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xED143022),
        Color(0xED38180C)
    ) // rgba(20,48,34,0.93) -> rgba(56,24,12,0.93)
)

val BottomBarBackgroundGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xF2FFD6C4),
        Color(0xF2B8F0D8)
    ) // rgba(255,214,196,0.95) -> rgba(184,240,216,0.95)
)
val DarkNavGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xF50F261A),
        Color(0xF530140A)
    ) // rgba(15,38,26,0.96) -> rgba(48,20,10,0.96)
)

val SelectedIconGradient = Brush.linearGradient(
    colors = listOf(
        AccentGreen,
        AccentOrange
    ) // Chat tab active: rgb(86,214,160) -> rgb(255,122,82)
)

val AddToWorkspaceGradient = Brush.linearGradient(
    colors = listOf(AccentLightGreen, AccentGreen) // rgb(140,232,196) -> rgb(86,214,160)
)

val SendButtonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFF9E7A), AccentOrange) // rgb(255,158,122) -> rgb(255,122,82)
)

data class ChipColors(
    val background: Color,
    val text: Color,
    val border: Color
)

// Spec Chip Colors (Derived from Figma App.tsx)
val specChipColorPaletteLight = listOf(
    ChipColors(
        background = Color(0x17FF7A52),
        text = AccentOrange,
        border = Color(0x30FF7A52)
    ), // Orange (0.09 = 0x17, 0.19 = 0x30)
    ChipColors(
        background = Color(0x1756D6A0),
        text = AccentGreen,
        border = Color(0x3056D6A0)
    ),  // Green
    ChipColors(
        background = Color(0x17FFB088),
        text = AccentPeach,
        border = Color(0x30FFB088)
    ),  // Peach
    ChipColors(
        background = Color(0x178CE8C4),
        text = AccentLightGreen,
        border = Color(0x308CE8C4)
    ) // Light Green
)

val specChipColorPaletteDark = listOf(
    ChipColors(
        background = Color(0x21FF7A52),
        text = AccentOrange,
        border = Color(0x38FF7A52)
    ), // Orange (0.13 = 0x21, 0.22 = 0x38)
    ChipColors(
        background = Color(0x2156D6A0),
        text = AccentGreen,
        border = Color(0x3856D6A0)
    ),  // Green
    ChipColors(
        background = Color(0x21FFB088),
        text = AccentPeach,
        border = Color(0x38FFB088)
    ),  // Peach
    ChipColors(
        background = Color(0x218CE8C4),
        text = AccentLightGreen,
        border = Color(0x388CE8C4)
    ) // Light Green
)

@Composable
fun getSpecChipColorPalette(): List<ChipColors> {
    return if (androidx.compose.foundation.isSystemInDarkTheme()) {
        specChipColorPaletteDark
    } else {
        specChipColorPaletteLight
    }
}