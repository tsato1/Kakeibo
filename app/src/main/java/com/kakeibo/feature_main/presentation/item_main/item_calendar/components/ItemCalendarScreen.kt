package com.kakeibo.feature_main.presentation.item_main.item_calendar.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.util.UtilDate.isWithinMonth
import kotlinx.datetime.*

@Composable
fun ItemCalendarScreen(
    navController: NavController,
    viewModel: ItemMainViewModel
) {
    val scaffoldState = rememberScaffoldState()

    val openSearchDetailDialog = remember { mutableStateOf(false) }
    val openExitSearchDialog = remember { mutableStateOf(false) }

    val searchIdState = viewModel.searchId
    val searchModel = viewModel.searchModel.value
    val dateState = viewModel.thisDate

    LaunchedEffect(Unit) {
        Log.d("asdf", "launchedEffect CALENDAR searchId="+searchIdState.value)
        if (searchIdState.value != -1L) {
            viewModel.onEvent(ItemMainEvent.LoadItems(searchIdState.value))
        }
    }

    Scaffold(
        floatingActionButton = {
            if (searchIdState.value == -1L) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ItemInputScreen.route)
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        bottomBar = { BottomBar(navController = navController) },
        scaffoldState = scaffoldState
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (searchIdState.value != -1L) {
                    SearchModeTopRow(
                        onCloseButtonClick = {
                            openExitSearchDialog.value = true
                        },
                        onTextButtonClick = {
                            openSearchDetailDialog.value = true
                        }
                    )
                } else {
                    DatePickerRow(
                        context = LocalContext.current,
                        type = DateType.YM,
                        dateFormatIndex = viewModel.dateFormatIndex,
                        onTextLayout = {
                            viewModel.onEvent(ItemMainEvent.DateChanged(it))
                        },
                        localDateState = dateState
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                CalendarRows(
                    viewModel = viewModel
                )
            }
        }
    }

    if (openExitSearchDialog.value) {
        ExitSearchAlertDialog(
            onDismissRequest = { openExitSearchDialog.value = false },
            onDismissButtonClick = {
                openExitSearchDialog.value = false
            },
            onConfirmButtonClick = {
                viewModel.onEvent(ItemMainEvent.ExitSearchMode)
                openExitSearchDialog.value = false
            }
        )
    }

    if (openSearchDetailDialog.value) {
        SearchDetailAlertDialog(
            onDismissRequest = { openSearchDetailDialog.value = false },
            onConfirmButtonClick = { openSearchDetailDialog.value = false },
            searchModel
        )
    }
}

@Composable
fun CalendarRows(
    viewModel: ItemMainViewModel
) {
    val showDetailDialog = remember { mutableStateOf(false) }
    val clickedIndex = rememberSaveable { mutableStateOf(0) }

    val listState = viewModel.calendarItemListState.value
    var iDate = viewModel.calendarFromDate.value // dates that gets put on calendar

    Column {
        for (i in 1..6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (j in 1..7) {
                    val index = (i - 1) * 7 + j - 1
                    val item = listState.calendarItemList[index]
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable {
                                when (item.parent.date.toLocalDate().monthNumber) {
                                    viewModel.thisDate.value.minus(
                                        1,
                                        DateTimeUnit.MONTH
                                    ).monthNumber -> {
                                        viewModel.onEvent(
                                            ItemMainEvent.DateChanged(
                                                viewModel.thisDate.value.minus(
                                                    1,
                                                    DateTimeUnit.MONTH
                                                )
                                            )
                                        )
                                    }
                                    viewModel.thisDate.value.monthNumber -> {
                                        showDetailDialog.value = true
                                        clickedIndex.value = index
                                    }
                                    viewModel.thisDate.value.plus(
                                        1,
                                        DateTimeUnit.MONTH
                                    ).monthNumber -> {
                                        viewModel.onEvent(
                                            ItemMainEvent.DateChanged(
                                                viewModel.thisDate.value.plus(1, DateTimeUnit.MONTH)
                                            )
                                        )
                                    }
                                }
                            }
                            .alpha(if (iDate.isWithinMonth(viewModel.thisDate.value)) 1f else 0.5f)
                    ) {
                        Text(text = "${iDate.dayOfMonth}")

                        if (listState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            if (item.parent.income.toInt() != 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colors.primary) // todo
                                            .size(2.dp, 10.dp)
                                    )
                                    Text(text = item.parent.income)
                                }
                            }
                            if (item.parent.expense.toInt() != 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MatchaGreen) // todo text size etc
                                            .size(2.dp, 10.dp)
                                    )
                                    Text(text = item.parent.expense)
                                }
                            }
                        }
                    }

                    iDate += DatePeriod(days = 1)
                }
            }
        }
    }

    if (showDetailDialog.value) {
        Dialog(onDismissRequest = { showDetailDialog.value = false } ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Text(text = listState.calendarItemList[clickedIndex.value].parent.date)
                Divider()
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listState.calendarItemList[clickedIndex.value].children) { item ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CategoryIcon(
                                code = item.categoryCode,
                                drawable = item.categoryDrawable,
                                image = item.categoryImage
                            )
                            Column() {
                                Text(text = item.categoryName)
                                Text(text = item.memo)
                            }
                            Text(text = item.amount)
                        }
                    }
                }
            }
        }
    }
}