package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kakeibo.R

@Composable
fun ExitSearchAlertDialog(
    onDismissRequest: () -> Unit,
    onDismissButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
        },
        onDismissRequest = { onDismissRequest() },
        text = {
            Text(text = stringResource(id = R.string.msg_exit_search))
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismissButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = { onConfirmButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}