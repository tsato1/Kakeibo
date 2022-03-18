package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kakeibo.R
import com.kakeibo.core.presentation.components.DialogCard

@Composable
fun ExitSearchDialog(
    onDismissRequest: () -> Unit,
    onDismissButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    DialogCard(
        onDismissRequest = onDismissRequest,
        title = "",
        content = {
            Text(text = stringResource(id = R.string.msg_exit_search))
        },
        positiveButton = {
            OutlinedButton(
                onClick = { onConfirmButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        negativeButton = {
            OutlinedButton(
                onClick = { onDismissButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}