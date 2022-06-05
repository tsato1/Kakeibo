package com.kakeibo.feature_main.presentation.item_main.item_calendar.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilDate.isWithinMonth
import kotlinx.datetime.*
import kotlin.math.roundToInt

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

    LaunchedEffect(Unit) {
        Log.d("asdf", "launchedEffect CALENDAR searchId="+searchIdState.value)
        if (searchIdState.value != 0L) {
            viewModel.onEvent(ItemMainEvent.LoadItems(searchIdState.value))
        }
    }

    Scaffold(
        floatingActionButton = {
            if (searchIdState.value == 0L) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.ItemInputScreen.route)
                    },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        bottomBar = { BottomBar(navController = navController) },
        scaffoldState = scaffoldState
    ) { innerPadding ->
        var offsetX by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    if (searchIdState.value == 0L) {
                        detectDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > 200 -> { viewModel.plus(-1, DateTimeUnit.MONTH) }
                                    offsetX < -200 -> { viewModel.plus(1, DateTimeUnit.MONTH) }
                                }
                                offsetX = 0f
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            when {
                                offsetX > 400f -> { offsetX = 400f }
                                offsetX < -400f -> { offsetX = -400f }
                            }
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (searchIdState.value != 0L) {
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
                        viewModel = viewModel
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (searchIdState.value == 0L) {
                    CalendarRows(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                else {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(id = R.string.calendar_view_not_available_in_search_mode),
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (viewModel.kkbAppState.value.intVal2 == ConstKkbAppDB.AD_SHOW) {
                BannerAds(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    adId = stringResource(id = R.string.main_banner_ad)
                )
            }
        }
    }

    if (openExitSearchDialog.value) {
        ExitSearchDialog(
            onDismissRequest = { openExitSearchDialog.value = false },
            onDismissButtonClick = {
                openExitSearchDialog.value = false
            },
            onConfirmButtonClick = {
                navController.navigate(Screen.ItemCalendarScreen.route + "?searchId=${0L}")
                viewModel.onEvent(ItemMainEvent.ExitSearchMode)
                openExitSearchDialog.value = false
            }
        )
    }

    if (openSearchDetailDialog.value) {
        SearchDetailDialog(
            onDismissRequest = { openSearchDetailDialog.value = false },
            onConfirmButtonClick = { openSearchDetailDialog.value = false },
            searchModel
        )
    }
}

@Composable
fun CalendarRows(
    navController: NavController,
    viewModel: ItemMainViewModel
) {
    val showDateDetailDialog = remember { mutableStateOf(false) }
    val clickedDateIndex = rememberSaveable { mutableStateOf(0) }

    val listState = viewModel.calendarItemListState.value
    var iDate = viewModel.calendarFromDate.value // dates that gets put on calendar

    Column {
        /* Day of Week Header */
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in 1..7) {
                Column(
                    modifier = Modifier
                        .height(28.dp)
                        .padding(2.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = stringArrayResource(id = R.array.week_name)[i - 1],
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = if (i == 1) {
                                Color.Red
                            } else if (i == 7) {
                                Color.Blue
                            } else {
                                Color.Black
                            }
                        )
                    )
                }
            }
        }
        Divider()
        for (i in 1..6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (j in 1..7) {
                    val index = (i - 1) * 7 + j - 1
                    val calendarItem = listState.calendarItemList[index]
                    Column(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                            .fillMaxSize()
                            .clickable {
                                when (calendarItem.parent.date.toLocalDate().monthNumber) {
                                    viewModel.localEventDate.value
                                        .toLocalDate()
                                        .minus(
                                            1,
                                            DateTimeUnit.MONTH
                                        ).monthNumber -> {
                                        viewModel.plus(-1, DateTimeUnit.MONTH)
                                    }
                                    viewModel.localEventDate.value.toLocalDate().monthNumber -> {
                                        showDateDetailDialog.value = true
                                        clickedDateIndex.value = index
                                    }
                                    viewModel.localEventDate.value
                                        .toLocalDate()
                                        .plus(
                                            1,
                                            DateTimeUnit.MONTH
                                        ).monthNumber -> {
                                        viewModel.plus(1, DateTimeUnit.MONTH)
                                    }
                                }
                            }
                            .alpha(
                                if (iDate.isWithinMonth(viewModel.localEventDate.value.toLocalDate()))
                                    1f
                                else 0.5f
                            )
                    ) {
                        if (iDate == UtilDate.getTodaysLocalDate())
                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Red),
                                text = "${iDate.dayOfMonth}"
                            )
                        else
                            Text(text = "${iDate.dayOfMonth}")

                        if (listState.isLoading) {
                            CircularProgressIndicator()
                        } else {
                            if (calendarItem.parent.income != "0") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_INCOME)
                                    Text(
                                        text = calendarItem.parent.income,
                                        style = TextStyle(fontSize = 10.sp)
                                    )
                                }
                            }
                            if (calendarItem.parent.expense != "0") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IncomeExpenseIndicator(categoryColor = UtilCategory.CATEGORY_COLOR_EXPENSE)
                                    Text(
                                        text = calendarItem.parent.expense,
                                        style = TextStyle(fontSize = 10.sp)
                                    )
                                }
                            }
                        }
                    }

                    iDate += DatePeriod(days = 1)
                }
            }
        }
    }

    if (showDateDetailDialog.value) {
        DialogCard(
            onDismissRequest = { showDateDetailDialog.value = false },
            title = listState.calendarItemList[clickedDateIndex.value].parent.date
                .toLocalDate()
                .toYMDString(UtilDate.DATE_FORMATS[viewModel.dateFormatIndex]),
            positiveButton = {
                OutlinedButton(
                    onClick = { showDateDetailDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.close))
                }
            },
            content = {
                if (listState.calendarItemList[clickedDateIndex.value].children.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(id = R.string.no_item_found))
                    }
                }
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(listState.calendarItemList[clickedDateIndex.value].children.size) { index ->
                            val item =
                                listState.calendarItemList[clickedDateIndex.value].children[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp, 2.dp)
                                    .clickable {
                                        navController.navigate(
                                            Screen.ItemDetailScreen.route + "?itemId=${item.id}"
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryIcon(
                                    code = item.categoryCode,
                                    drawable = item.categoryDrawable,
                                    image = item.categoryImage
                                )
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp, 2.dp)
                                ) {
                                    Text(text = item.categoryName)
                                    Text(text = item.memo)
                                }
                                Text(text = item.amount)
                            }
                        }
                    }
                }
            }
        )
    }
}