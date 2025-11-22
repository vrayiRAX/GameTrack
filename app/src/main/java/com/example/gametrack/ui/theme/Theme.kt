package com.example.gametrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonMagenta,
    onPrimary = Color.Black,

    secondary = NeonGold,
    onSecondary = Color.Black,

    tertiary = CyberCyan,
    onTertiary = Color.Black,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,

    outline = BorderNeonGold,
    outlineVariant = BorderMagenta
)

@Composable
fun GameTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
