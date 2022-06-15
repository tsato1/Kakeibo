package com.kakeibo.feature_main.presentation.common.components

import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kakeibo.R
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.domain.models.DisplayedItemModel

@Composable
fun ItemDeleteDialog(
    item: DisplayedItemModel,
    onDismissRequest: () -> Unit,
    onDeleteButtonClicked: () -> Unit
) {
    DialogCard(
        onDismissRequest = { onDismissRequest() },
        title = stringResource(id = R.string.delete),
        content = { Text(text = stringResource(id = R.string.quest_do_you_want_to_delete_item)) },
        positiveButton = {
            OutlinedButton(
                onClick = { onDeleteButtonClicked() }
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        negativeButton = {
            OutlinedButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}