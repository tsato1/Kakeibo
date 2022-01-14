package com.kakeibo.feature_main.presentation.item_detail.item_edit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import com.kakeibo.feature_main.presentation.item_detail.item_input.DisplayedCategoryListState

@Composable
fun CategoryListDialog(
    modifier: Modifier = Modifier,
    displayedCategoryListState: DisplayedCategoryListState,
    onDismiss: () -> Unit,
    onItemClick: (DisplayedCategoryModel) -> Unit
) {

    AlertDialog(
        modifier = modifier,
        title = {
        },
        onDismissRequest = { onDismiss() },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(displayedCategoryListState.displayedCategoryList) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 4.dp)
                            .clickable {
                                onItemClick(item)
                                onDismiss()
                            }
                    ) {
                        CategoryIcon(code = item.code, drawable = item.drawable, image = item.image)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = item.name)
                    }
                    Divider()
                }
            }
        },
        dismissButton = {},
        confirmButton = {}
    )
}