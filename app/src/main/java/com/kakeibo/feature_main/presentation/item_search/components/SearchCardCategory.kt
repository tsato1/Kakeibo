package com.kakeibo.feature_main.presentation.item_search.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
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
import com.kakeibo.feature_main.presentation.item_search.CATEGORY_NOT_CHOSEN
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
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(60.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                openCategoryPickerDialog.value = true
            }
        ) {
            if (searchCardCategoryState.value.categoryModel.code == CATEGORY_NOT_CHOSEN) {
                Text(text = stringResource(id = R.string.category))
            }
            else {
                CategoryIcon(
                    code = searchCardCategoryState.value.categoryModel.code,
                    drawable = searchCardCategoryState.value.categoryModel.drawable,
                    image = searchCardCategoryState.value.categoryModel.image
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = searchCardCategoryState.value.categoryModel.name)
            }
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