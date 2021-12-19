package com.kakeibo.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = VividRed,
    background = ThickCream,
    onBackground = LightGray, // hint text
    surface = LightCream,
    onSurface = MatchaGreen
)

private val DarkColorPalette = darkColors(
    onBackground = Color.White,
)

@Composable
fun KakeiboTheme(darkTheme: Boolean = true, content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}