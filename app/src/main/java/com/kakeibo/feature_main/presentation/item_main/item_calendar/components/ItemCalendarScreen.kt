package com.kakeibo.feature_main.presentation.item_main.item_calendar.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItemListState
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.util.UtilDate

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
    val firstDay = viewModel.firstDayOfMonth.value

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
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                CalendarRows(
                    expandableItemListState = viewModel.expandableItemListState.value,
                    firstDay = firstDay
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
    expandableItemListState: ExpandableItemListState,
    firstDay: Int
) {
    val list = expandableItemListState.expandableItemList
    val iterator = list.listIterator()

    Column {
        for (i in 1..6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (j in 1..7) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        val date = j + (i - 1) * 7 - firstDay
                        if (1 <= date && date <=
                            UtilDate.getLastDayOfMonth(UtilDate.getTodaysLocalDate().toString())
                        ) {
                            Text(text = "${date}")

                            val currSlotNum = (i - 1) * 7 + j
                            if (iterator.hasNext()) {
                                val item = iterator.next()
                                val itemDate = item.parent.date
                                val itemDateDate = itemDate.substring(itemDate.length - 2).toInt()
                                if (itemDateDate == currSlotNum - firstDay) {
                                    if (expandableItemListState.isLoading) {
                                        CircularProgressIndicator()
                                    } else {
                                        if (item.parent.income.toInt() != 0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(modifier = Modifier
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
                                                Box(modifier = Modifier
                                                    .background(MatchaGreen) // todo size etc
                                                    .size(2.dp, 10.dp)
                                                )
                                                Text(text = item.parent.expense)
                                            }
                                        }
                                    }
                                } else {
                                    iterator.previous()
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray)
                            ) {

                            }
                        }
                    }
                }
            }
        }
    }
}
 /*
 currSlotNum

 Sun Mon Tue Wed Thu Fri Sat
  1   2   3   4   5   6   7
  8   9  10  11  12  13  14
 15  ...

 if firstDay == 4 (Thu), currSlotNum - firstDay
 Sun Mon Tue Wed Thu Fri Sat
  -3  -2  -1  0   1   2   3
  */