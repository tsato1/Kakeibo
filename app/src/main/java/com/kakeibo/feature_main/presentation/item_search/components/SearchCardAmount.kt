package com.kakeibo.feature_main.presentation.item_search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.core.presentation.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_search.ItemSearchEvent
import com.kakeibo.feature_main.presentation.item_search.ItemSearchViewModel
import com.kakeibo.util.UtilText

@ExperimentalComposeUiApi
@Composable
fun SearchCardAmount(
    modifier: Modifier = Modifier,
    viewModel: ItemSearchViewModel
) {

    val itemAmountState = viewModel.searchCardAmountState

    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.amount))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = itemAmountState.value.from,
                hint = itemAmountState.value.fromHint,
                onValueChange = {
                    if (it.length <= 10 && UtilText.isAmountValid(it, viewModel.fractionDigits))
                        viewModel.onEvent(ItemSearchEvent.AmountFromEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemSearchEvent.AmountFromFocusChanged(it))
                },
                isHintVisible = itemAmountState.value.isFromHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "-"
            )
            TransparentHintTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = itemAmountState.value.to,
                hint = itemAmountState.value.toHint,
                onValueChange = {
                    if (it.length <= 10 && UtilText.isAmountValid(it, viewModel.fractionDigits))
                        viewModel.onEvent(ItemSearchEvent.AmountToEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemSearchEvent.AmountToFocusChanged(it))
                },
                isHintVisible = itemAmountState.value.isToHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
        }
    }
}