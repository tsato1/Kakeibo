package com.kakeibo.feature_main.presentation.item_search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_search.ItemSearchEvent
import com.kakeibo.feature_main.presentation.item_search.ItemSearchViewModel

@ExperimentalComposeUiApi
@Composable
fun SearchCardMemo(
    modifier: Modifier = Modifier,
    viewModel: ItemSearchViewModel
) {

    val itemMemoState = viewModel.searchCardMemoState

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.memo))
        TransparentHintTextField(
            modifier = Modifier.fillMaxWidth(),
            text = itemMemoState.value.memo,
            hint = itemMemoState.value.hint,
            onValueChange = {
                if (it.length <= 20)
                    viewModel.onEvent(ItemSearchEvent.MemoEntered(it))
            },
            onFocusChange = {
                viewModel.onEvent(ItemSearchEvent.MemoFocusChanged(it))
            },
            isHintVisible = itemMemoState.value.isHintVisible,
            singleLine = true,
            textStyle = MaterialTheme.typography.body1
        )
    }
}