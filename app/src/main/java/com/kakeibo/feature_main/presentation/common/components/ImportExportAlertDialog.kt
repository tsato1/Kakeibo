package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.util.UtilExport
import com.kakeibo.util.UtilFiles

@Composable
fun ImportExportAlertDialog(
    onConfirmButtonClick: () -> Unit,
    onDismissRequest: () -> Unit,
    itemMainViewModel: ItemMainViewModel
) {
    val context = LocalContext.current

    val itemList = itemMainViewModel.expandableItemListState.value.expandableItemList

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "Export")
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = stringResource(id = R.string.export))
            }
        },
        onDismissRequest = { onDismissRequest() },
        text = {
            Text(text = stringResource(id = R.string.quest_export_this_report_D))
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    UtilExport.buildOrderByDate(UtilFiles.FILE_NAME, context, itemList)
                    onConfirmButtonClick()
                }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        }
    )

}
