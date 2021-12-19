package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SaveCancelBottomBar(
    modifier: Modifier = Modifier,
    textLeft: String = "Cancel",
    textRight: String = "Save",
    onLeftButtonClick: () -> Unit,
    onRightButtonClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(
            onClick = onLeftButtonClick
        ) {
            Text(
                text = textLeft
            )
        }
        Text(
            text = ""
        )
        TextButton(
            onClick = onRightButtonClick
        ) {
            Text(
                text = textRight
            )
        }
    }
}