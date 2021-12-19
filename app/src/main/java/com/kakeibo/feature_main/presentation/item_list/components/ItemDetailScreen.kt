package com.kakeibo.feature_main.presentation.item_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.core.util.UiEvent
import com.kakeibo.feature_main.presentation.common.components.SaveCancelBottomBar
import com.kakeibo.feature_main.presentation.common.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_input.ItemInputEvent
import com.kakeibo.feature_main.presentation.item_input.ItemInputViewModel
import com.kakeibo.R
import kotlinx.coroutines.flow.collectLatest

@ExperimentalComposeUiApi
@Composable
fun ItemDetailScreen(
    navController: NavController,
    viewModel: ItemInputViewModel = hiltViewModel()
) {
    val dateState = viewModel.date
    val amountState = viewModel.itemAmount.value
    val memoState = viewModel.itemMemo.value
    val categoryIdState = viewModel.itemCategoryCode
    val displayedCategoriesState = viewModel.displayedCategoryListState.value

    val scaffoldState = rememberScaffoldState()

    val gridListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Save -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Event Date: " // todo; translate
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Amount: "
                )
                TransparentHintTextField(
                    text = amountState.text,
                    hint = amountState.hint,
                    onValueChange = {
                        viewModel.onEvent(ItemInputEvent.EnterAmount(it))
                    },
                    onFocusChange = {
                        viewModel.onEvent(ItemInputEvent.ChangeAmountFocus(it))
                    },
                    isHintVisible = amountState.isHintVisible,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.h6
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Memo: "
                )
                TransparentHintTextField(
                    text = memoState.text,
                    hint = memoState.hint,
                    onValueChange = {
                        viewModel.onEvent(ItemInputEvent.EnterMemo(it))
                    },
                    onFocusChange = {
                        viewModel.onEvent(ItemInputEvent.ChangeMemoFocus(it))
                    },
                    isHintVisible = memoState.isHintVisible,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.h6
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.weight(1f))
            SaveCancelBottomBar(
                modifier = Modifier.fillMaxWidth(),
                textLeft = stringResource(id = R.string.cancel),
                textRight = stringResource(id = R.string.save),
                onLeftButtonClick = { navController.navigateUp() },
                onRightButtonClick = { navController.navigateUp() }
            )
        }
    }
}