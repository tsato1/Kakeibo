package com.kakeibo.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimen(
    val default: Dp = 0.dp,

    val dialogDefaultHeight: Dp = 300.dp,
    val dialogWithListHeight: Dp = 500.dp,
    val dialogRoundedCorner: Dp = 12.dp,
    val dialogPadding: Dp = 8.dp,
    val dialogTitlePaddingHorizontal: Dp = 8.dp,
    val dialogTitlePaddingVertical: Dp = 14.dp
)

val LocalDimen = compositionLocalOf { Dimen() }

val MaterialTheme.dimens: Dimen
    @Composable
    @ReadOnlyComposable
    get() = LocalDimen.current