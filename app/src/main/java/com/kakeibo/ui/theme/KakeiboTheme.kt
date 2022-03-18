package com.kakeibo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
fun KakeiboTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    if (darkTheme) {
        DarkColorPalette
    }
    else {
        LightColorPalette
    }

    CompositionLocalProvider(
        LocalDimen provides Dimen()
    ) {
        MaterialTheme(
            colors = LightColorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}