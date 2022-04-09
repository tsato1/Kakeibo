package com.kakeibo.feature_main.presentation.item_detail.item_edit.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.feature_main.presentation.common.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailEvent
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailViewModel
import com.kakeibo.util.UtilText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.DateTimeUnit
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@Composable
fun ItemEditScreen(
    navController: NavController,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val itemAmountState = viewModel.itemAmount
    val itemCategoryCode = viewModel.itemCategoryCode
    val itemCategoryName = viewModel.itemCategoryName
    val itemCategoryDrawable = viewModel.itemCategoryDrawable
    val itemCategoryImage = viewModel.itemCategoryImage
    val itemMemoState = viewModel.itemMemo
    val displayedCategoryListState = viewModel.displayedCategoryListState

    val openDialogState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemDetailViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is ItemDetailViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ItemDetailViewModel.UiEvent.Save -> {
                    Toast.makeText(context, R.string.msg_item_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) {
        var offsetX by remember { mutableStateOf(0f) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 200 -> { viewModel.plus(-1, DateTimeUnit.DAY) }
                                offsetX < -200 -> { viewModel.plus(1, DateTimeUnit.DAY) }
                            }
                            offsetX = 0f
                        }
                    ) { change, dragAmount ->
                        change.consumeAllChanges()
                        offsetX += dragAmount.x
                        when {
                            offsetX > 400f -> { offsetX = 400f }
                            offsetX < -400f -> { offsetX = -400f }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date ================================================================================
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.event_date_colon)
            )
            DatePickerRow(
                modifier = Modifier.fillMaxWidth(),
                context = LocalContext.current,
                type = DateType.YMDW,
                dateFormatIndex = viewModel.dateFormatIndex,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Amount ==============================================================================
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.amount_colon)
            )
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = itemAmountState.value.text,
                hint = itemAmountState.value.hint,
                onValueChange = {
                    if (it.length <= 10 && UtilText.isAmountValid(it, viewModel.fractionDigits))
                        viewModel.onEvent(ItemDetailEvent.AmountEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemDetailEvent.AmountFocusChanged(it))
                },
                isHintVisible = itemAmountState.value.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Memo ================================================================================
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.memo_colon)
            )
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = itemMemoState.value.text,
                hint = itemMemoState.value.hint,
                onValueChange = {
                    if (it.length <= 20)
                        viewModel.onEvent(ItemDetailEvent.MemoEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemDetailEvent.MemoFocusChanged(it))
                },
                isHintVisible = itemMemoState.value.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Category ============================================================================
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(id = R.string.category_colon)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openDialogState.value = true
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryIcon(
                    modifier = Modifier.padding(10.dp),
                    code = itemCategoryCode.value,
                    drawable = itemCategoryDrawable.value,
                    image = itemCategoryImage.value
                )
                Text(
                    text = itemCategoryName.value
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Save Button =========================================================================
            Button(
                onClick = { viewModel.onEvent(ItemDetailEvent.SaveItem) }
            ) {
                Text(
                    text = stringResource(id = R.string.save)
                )
            }
        }
    }


    if (openDialogState.value) {
        CategoryListDialog(
            modifier = Modifier.height(400.dp),
            displayedCategoryListState = displayedCategoryListState.value,
            onItemClick = {
                viewModel.onEvent(ItemDetailEvent.CategorySelected(it))
            },
            onDismiss = { openDialogState.value = false },
        )
    }

}