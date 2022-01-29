package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.Column
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
fun SearchDetailAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
            Text(text = stringResource(id = R.string.search_criteria))
        },
        onDismissRequest = { onDismissRequest() },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.date_range))

                Text(text = stringResource(id = R.string.amount_range))
                Text(text = stringResource(id = R.string.category))
                Text(text = stringResource(id = R.string.memo))
            }
        },
        dismissButton = {},
        confirmButton = {
            OutlinedButton(
                onClick = { onConfirmButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    )
}