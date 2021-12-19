package com.kakeibo.feature_main.presentation.item_input.components

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.util.UiEvent
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.feature_main.presentation.common.components.TransparentHintTextField
import com.kakeibo.feature_main.presentation.item_input.GridCategoryItem
import com.kakeibo.feature_main.presentation.item_input.ItemInputEvent
import com.kakeibo.feature_main.presentation.item_input.ItemInputViewModel
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.UtilText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ItemInputScreen(
    navController: NavController,
    viewModel: ItemInputViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val gridListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val amountState = viewModel.itemAmount.value
    val memoState = viewModel.itemMemo.value
    val displayedCategoriesState = viewModel.displayedCategoryListState.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is UiEvent.Save -> {
                    Toast.makeText(context, R.string.msg_item_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigate(
                        Screen.ItemListScreen.route + "?itemId=${viewModel.savedItemId.value}"
                    )
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
            DatePickerRow(
                context = LocalContext.current,
                viewModel = viewModel,
                type = DateType.YMDW
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = amountState.text,
                hint = amountState.hint,
                onValueChange = {
                    if (it.length <= 10 && UtilText.isAmountValid(it))
                        viewModel.onEvent(ItemInputEvent.EnterAmount(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemInputEvent.ChangeAmountFocus(it))
                },
                isHintVisible = amountState.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                modifier = Modifier.fillMaxWidth(),
                text = memoState.text,
                hint = memoState.hint,
                onValueChange = {
                    if (it.length <= 20)
                        viewModel.onEvent(ItemInputEvent.EnterMemo(it))
                },
                onFocusChange = {
                    viewModel.onEvent(ItemInputEvent.ChangeMemoFocus(it))
                },
                isHintVisible = memoState.isHintVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                cells = GridCells.Fixed(
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
                            displayedCategory = category,
                            onItemClick = {
                                coroutineScope.launch {
                                    viewModel.onEvent(
                                        ItemInputEvent.SaveItemWithCategory(category)
                                    )
                                }
                            },
                            onItemLongClick = {}
                        )
                    }
                }
            )
        }
    }
}