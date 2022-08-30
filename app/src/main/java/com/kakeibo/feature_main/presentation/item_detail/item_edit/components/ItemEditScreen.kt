package com.kakeibo.feature_main.presentation.item_detail.item_edit.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.core.presentation.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailEvent
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailViewModel
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.isAmountValid
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.DateTimeUnit
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalComposeUiApi
@Composable
fun ItemEditScreen(
    navController: NavController,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val itemAmountState = viewModel.itemAmountState
    val itemCategoryCodeState = viewModel.itemCategoryCodeState
    val itemCategoryNameState = viewModel.itemCategoryNameState
    val itemCategoryDrawableState = viewModel.itemCategoryDrawableState
    val itemCategoryImageState = viewModel.itemCategoryImageState
    val itemMemoState = viewModel.itemMemoState
    val displayedCategoryListState = viewModel.displayedCategoryListState

    val openDialogState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemDetailViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
                is ItemDetailViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG).show()
                }
                is ItemDetailViewModel.UiEvent.Save -> {
                    Toast.makeText(context, R.string.msg_item_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigate(
                        Screen.ItemListScreen.route + "?searchId=${0L}/?focusDate=${event.focusDate}/?focusItemId=${event.focusItemId}/?reload=${true}"
                    ) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
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
                                offsetX > 200 -> {
                                    viewModel.plus(-1, DateTimeUnit.DAY)
                                }
                                offsetX < -200 -> {
                                    viewModel.plus(1, DateTimeUnit.DAY)
                                }
                            }
                            offsetX = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        when {
                            offsetX > 400f -> {
                                offsetX = 400f
                            }
                            offsetX < -400f -> {
                                offsetX = -400f
                            }
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
                    if (it.length <= 10 && isAmountValid(it, viewModel.fractionDigits))
                        viewModel.onEvent(ItemDetailEvent.AmountEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemDetailEvent.AmountFocusChanged(it))
                },
                isHintVisible = itemAmountState.value.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                    code = itemCategoryCodeState.value,
                    drawable = itemCategoryDrawableState.value,
                    image = itemCategoryImageState.value
                )
                Text(
                    text = itemCategoryNameState.value
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            // Save Button =========================================================================
            Button(
                onClick = {
                    viewModel.onEvent(ItemDetailEvent.SaveItem)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.save)
                )
            }
            if (viewModel.kkbAppModelState.value.kkbAppModel.intVal2 == ConstKkbAppDB.AD_SHOW) {
                BannerAds(
                    adId = stringResource(id = R.string.main_banner_ad)
                )
            }
        }
    }


    if (openDialogState.value) {
        DialogCard(
            onDismissRequest = { openDialogState.value = false },
            title = stringResource(id = R.string.choose_category),
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(displayedCategoryListState.value.displayedCategoryList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                                .clickable {
                                    viewModel.onEvent(ItemDetailEvent.CategorySelected(item))
                                    openDialogState.value = false
                                }
                        ) {
                            CategoryIcon(
                                code = item.code,
                                drawable = item.drawable,
                                image = item.image
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = item.name)
                        }
                        Divider()
                    }
                }
            },
            negativeButton = {
                OutlinedButton(
                    onClick = { openDialogState.value = false }
                ) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        )
    }

}