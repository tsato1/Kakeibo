package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kakeibo.R
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.util.UtilExport
import com.kakeibo.util.UtilFiles

@Composable
fun ImportExportDialog(
    onConfirmButtonClick: () -> Unit,
    onDismissRequest: () -> Unit,
    itemMainViewModel: ItemMainViewModel
) {
    val context = LocalContext.current

    val itemList = itemMainViewModel.expandableItemListState.value.expandableItemList

    DialogCard(
        onDismissRequest = { onDismissRequest() },
        title = stringResource(id = R.string.export),
        content = {
            Text(text = stringResource(id = R.string.quest_export_this_report_D))
        },
        negativeButton = {
            OutlinedButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        positiveButton = {
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
