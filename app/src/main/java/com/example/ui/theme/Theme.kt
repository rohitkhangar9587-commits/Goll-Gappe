package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PuriGold,
    secondary = PudinaGreen,
    tertiary = ImliRed,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PuriAmber,
    secondary = PudinaGreen,
    tertiary = ImliRed,
    background = Color(0xFFFFF8E1),
    surface = Color(0xFFFFF3E0),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
