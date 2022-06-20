package com.kakeibo.feature_main.presentation.item_detail.item_input.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.kakeibo.core.presentation.components.GridCategoryItem
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.core.presentation.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailEvent
import com.kakeibo.feature_main.presentation.item_detail.ItemDetailViewModel
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.UtilText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ItemInputScreen(
    navController: NavController,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val gridListState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val amountState = viewModel.itemAmountState.value
    val memoState = viewModel.itemMemoState.value
    val displayedCategoriesState = viewModel.displayedCategoryListState.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemDetailViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
                is ItemDetailViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message.asString(context), Toast.LENGTH_LONG)
                        .show()
                }
                is ItemDetailViewModel.UiEvent.Save -> {
                    Toast.makeText(context, R.string.msg_item_successfully_saved, Toast.LENGTH_LONG)
                        .show()
                    navController.navigate(
                        Screen.ItemListScreen.route +
                                "?searchId=${0L}/?focusDate=${event.focusDate}/?focusItemId=${event.focusItemId}"
                    )
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
                }
        ) {
            DatePickerRow(
                context = LocalContext.current,
                type = DateType.YMDW,
                dateFormatIndex = viewModel.dateFormatIndex,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = amountState.text,
                hint = amountState.hint,
                onValueChange = {
                    if (it.length <= 10 && UtilText.isAmountValid(it, viewModel.fractionDigits))
                        viewModel.onEvent(ItemDetailEvent.AmountEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemDetailEvent.AmountFocusChanged(it))
                },
                isHintVisible = amountState.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = memoState.text,
                hint = memoState.hint,
                onValueChange = {
                    if (it.length <= 20)
                        viewModel.onEvent(ItemDetailEvent.MemoEntered(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemDetailEvent.MemoFocusChanged(it))
                },
                isHintVisible = memoState.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(
                    count = viewModel.appPreferences.getNumColumns()
                ),
                state = gridListState,
                contentPadding = PaddingValues(
                    start = 6.dp,
                    top = 8.dp,
                    end = 6.dp,
                    bottom = 8.dp
                ),
                content = {
                    items(displayedCategoriesState.displayedCategoryList.size) { index ->
                        val category = displayedCategoriesState.displayedCategoryList[index]

                        GridCategoryItem(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            categoryModel = category,
                            onItemClick = {
                                coroutineScope.launch {
                                    viewModel.onEvent(ItemDetailEvent.CategorySelected(category))
                                    viewModel.onEvent(ItemDetailEvent.SaveItem)
                                }
                            },
                            onItemLongClick = {}
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            if (viewModel.kkbAppModelState.value.kkbAppModel.intVal2 == ConstKkbAppDB.AD_SHOW) {
                BannerAds(
                    adId = stringResource(id = R.string.main_banner_ad)
                )
            }
        }
    }
}