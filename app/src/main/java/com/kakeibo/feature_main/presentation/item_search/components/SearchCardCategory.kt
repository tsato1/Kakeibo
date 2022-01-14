package com.kakeibo.feature_main.presentation.item_search.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.feature_main.presentation.item_search.ItemSearchViewModel
import com.kakeibo.R
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.item_detail.item_edit.components.CategoryListDialog
import com.kakeibo.feature_main.presentation.item_search.ItemSearchEvent

@Composable
fun SearchCardCategory(
    modifier: Modifier = Modifier,
    viewModel: ItemSearchViewModel
) {
    val openCategoryPickerDialog = remember { mutableStateOf(false) }

    val searchCardCategoryState = viewModel.searchCardCategoryState
    val displayedCategoryListState = viewModel.displayedCategoryListState

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.category))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                openCategoryPickerDialog.value = true
            }
        ) {
            searchCardCategoryState.value.categoryModel?.let {
                CategoryIcon(
                    code = it.code,
                    drawable = it.drawable,
                    image = it.image
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = it.name)
            } ?: Text(text = stringResource(id = R.string.category))
        }
    }

    if (openCategoryPickerDialog.value) {
        CategoryListDialog(
            modifier = Modifier.height(400.dp),
            displayedCategoryListState = displayedCategoryListState.value,
            onDismiss = { openCategoryPickerDialog.value = false },
            onItemClick = {
                viewModel.onEvent(ItemSearchEvent.CategorySelected(it))
            }
        )
    }

}