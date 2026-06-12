/*
 * Tujuan: Menetapkan design system Material 3 Version 2 untuk light dan dark mode.
 * Caller: MainActivity.
 * Dependensi: Jetpack Compose Material 3.
 * Main Functions: VocabTheme.
 * Side Effects: Mengubah tema visual seluruh composable di bawahnya.
 */
package com.kosakata.inggris.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF155EEF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF001A43),
    secondary = Color(0xFF087F5B),
    secondaryContainer = Color(0xFFB8F2D9),
    tertiary = Color(0xFF735C00),
    background = Color(0xFFF7F8FC),
    surface = Color(0xFFF7F8FC),
    surfaceVariant = Color(0xFFE2E7F0),
    outline = Color(0xFF74777F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFB4C8FF),
    onPrimary = Color(0xFF002D6D),
    primaryContainer = Color(0xFF00439A),
    secondary = Color(0xFF85D6B6),
    secondaryContainer = Color(0xFF00513A),
    tertiary = Color(0xFFE6C349),
    background = Color(0xFF111318),
    surface = Color(0xFF111318),
    surfaceVariant = Color(0xFF44474F)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun VocabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography(),
        shapes = AppShapes,
        content = content
    )
}
