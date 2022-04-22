package com.kakeibo.feature_main.presentation.item_main.item_chart.components

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.ui.theme.VividRed
import com.kakeibo.util.UtilCategory
import kotlinx.datetime.DateTimeUnit
import java.math.BigDecimal
import kotlin.math.roundToInt

@Composable
fun ItemChartScreen(
    navController: NavController,
    viewModel: ItemMainViewModel
) {
    val scaffoldState = rememberScaffoldState()

    val openSearchDetailDialog = remember { mutableStateOf(false) }
    val openExitSearchDialog = remember { mutableStateOf(false) }
    val openDetailListDialog = remember { mutableStateOf(false) }
    val detailListFlag = remember { mutableStateOf(UtilCategory.CATEGORY_COLOR_INCOME) }
    val clickedCategoryCode = remember { mutableStateOf(0) }
    val clickedCategoryName = remember { mutableStateOf("") }

    val itemChartState = viewModel.itemChartState
    val searchIdState = viewModel.searchId
    val searchModel = viewModel.searchModel.value

    LaunchedEffect(Unit) {
        Log.d("asdf", "launchedEffect CHART searchId="+searchIdState.value)
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
        var offsetX by remember { mutableStateOf(0f) }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 200 -> { viewModel.plus(-1, DateTimeUnit.MONTH) }
                                offsetX < -200 -> { viewModel.plus(1, DateTimeUnit.MONTH) }
                            }
                            offsetX = 0f
                        }
                    ) { change, dragAmount ->
                        change.consumeAllChanges()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Summary =========================================================================
                    // Income
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = stringResource(id = R.string.income)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = itemChartState.value.incomeTotal
                        )
                    }
                    // Expense
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = stringResource(id = R.string.expense_colon)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = itemChartState.value.expenseTotal
                        )
                    }
                    // Balance
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 8.dp, 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        )
                        Text(
                            text = stringResource(id = R.string.balance_colon)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = (itemChartState.value.incomeTotal.toDouble() - itemChartState.value.expenseTotal.toDouble()).toString()
                        )
                    }
                    Divider()
                    // Bar chart Balance ===================================================================
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(6.dp)
//                            .border(
//                                width = 1.dp,
//                                color = Color.Black,
//                                shape = RoundedCornerShape(4.dp)
//                            ),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            modifier = Modifier.padding(vertical = 4.dp),
//                            text = stringResource(id = R.string.balance_colon)
//                        )
//                    }
                    // Pie chart Income ====================================================================
                    if (itemChartState.value.incomeList.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (itemChartState.value.incomeList.isNotEmpty()) {
                                    Text(text = stringResource(id = R.string.income_colon))
                                }
                                PieChart(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(6.dp),
                                    pieChartData = PieChartData(
                                        slices = itemChartState.value.incomeList.map {
                                            PieChartData.Slice(it.amount.toFloat(), VividRed)
                                        }
                                    )
                                )
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(
                                            (dimensionResource(id = R.dimen.category_list_row_height) + 2.dp) *
                                                    itemChartState.value.incomeList.size
                                        )
                                ) {
                                    items(itemChartState.value.incomeList.size) { index ->
                                        val displayedItemModel =
                                            itemChartState.value.incomeList[index]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(dimensionResource(id = R.dimen.category_list_row_height))
                                                .clickable {
                                                    openDetailListDialog.value = true
                                                    detailListFlag.value =
                                                        UtilCategory.CATEGORY_COLOR_INCOME
                                                    clickedCategoryCode.value =
                                                        displayedItemModel.categoryCode
                                                    clickedCategoryName.value =
                                                        displayedItemModel.categoryName
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CategoryIcon(
                                                modifier = Modifier.padding(2.dp),
                                                code = displayedItemModel.categoryCode,
                                                drawable = displayedItemModel.categoryDrawable,
                                                image = displayedItemModel.categoryImage
                                            )
                                            Text(
                                                text = displayedItemModel.categoryName
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                modifier = Modifier.padding(end = 4.dp),
                                                text = displayedItemModel.amount
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(3.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(VividRed)
                                                    .aspectRatio(1f)
                                            )
                                            Text(
                                                modifier = Modifier.width(55.dp),
                                                text = "${
                                                    displayedItemModel.amount.toBigDecimal()
                                                        .times(BigDecimal(100))
                                                        .divide(
                                                            itemChartState.value.incomeTotal.toBigDecimal(),
                                                            0,
                                                            BigDecimal.ROUND_HALF_DOWN
                                                        )
                                                }%",
                                                textAlign = TextAlign.End
                                            )
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                    // Pie chart Expense ===================================================================
                    if (itemChartState.value.expenseList.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (itemChartState.value.expenseList.isNotEmpty()) {
                                    Text(text = stringResource(id = R.string.expense_colon))
                                }
                                PieChart(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .padding(6.dp),
                                    pieChartData = PieChartData(
                                        slices = itemChartState.value.expenseList.map {
                                            PieChartData.Slice(it.amount.toFloat(), MatchaGreen)
                                        }
                                    )
                                )
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .height(
                                            (dimensionResource(id = R.dimen.category_list_row_height) + 2.dp) *
                                                    itemChartState.value.expenseList.size
                                        )
                                ) {
                                    items(itemChartState.value.expenseList.size) { index ->
                                        val displayedItemModel =
                                            itemChartState.value.expenseList[index]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(dimensionResource(id = R.dimen.category_list_row_height))
                                                .clickable {
                                                    openDetailListDialog.value = true
                                                    detailListFlag.value =
                                                        UtilCategory.CATEGORY_COLOR_EXPENSE
                                                    clickedCategoryCode.value =
                                                        displayedItemModel.categoryCode
                                                    clickedCategoryName.value =
                                                        displayedItemModel.categoryName
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CategoryIcon(
                                                modifier = Modifier.padding(2.dp),
                                                code = displayedItemModel.categoryCode,
                                                drawable = displayedItemModel.categoryDrawable,
                                                image = displayedItemModel.categoryImage
                                            )
                                            Text(
                                                text = displayedItemModel.categoryName
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                modifier = Modifier.padding(end = 4.dp),
                                                text = displayedItemModel.amount
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(3.dp)
                                                    .clip(RoundedCornerShape(2.dp))
                                                    .background(MatchaGreen)
                                                    .aspectRatio(1f)
                                            )
                                            Text(
                                                modifier = Modifier.width(55.dp),
                                                text = "${
                                                    displayedItemModel.amount.toBigDecimal()
                                                        .times(BigDecimal(100))
                                                        .divide(
                                                            itemChartState.value.expenseTotal.toBigDecimal(),
                                                            0,
                                                            BigDecimal.ROUND_HALF_DOWN
                                                        )
                                                }%",
                                                textAlign = TextAlign.End
                                            )
                                        }
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
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
                navController.navigate(Screen.ItemChartScreen.route + "?searchId=${0L}")
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

    if (openDetailListDialog.value) {
        Dialog(
            onDismissRequest = { openDetailListDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp, 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_mikan),
                        contentDescription = "",
                        tint= Color.Unspecified
                    )
                    Text(text = clickedCategoryName.value)
                }
                Divider()
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val list = when (detailListFlag.value) {
                        UtilCategory.CATEGORY_COLOR_INCOME -> {
                            itemChartState.value.incomeMap[clickedCategoryCode.value]
                        }
                        UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                            itemChartState.value.expenseMap[clickedCategoryCode.value]
                        }
                        else -> emptyList()
                    }
                    list?.let {
                        items(list) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp, 6.dp)
                                    .clickable {
                                        navController.navigate(
                                            Screen.ItemDetailScreen.route + "?itemId=${item.id}"
                                        )
                                    },
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row {
                                    Text(text = stringResource(id = R.string.event_date_colon))
                                    Text(text = item.eventDate)
                                }
                                Row {
                                    Text(text = stringResource(id = R.string.category_colon))
                                    Text(text = item.categoryName)
                                }
                                Row {
                                    Text(text = stringResource(id = R.string.amount_colon))
                                    Text(text = item.amount)
                                }
                                Row {
                                    Text(text = stringResource(id = R.string.memo_colon))
                                    Text(text = item.memo)
                                }
                                Row {
                                    Text(text = stringResource(id = R.string.updated_on_colon))
                                    Text(text = item.updateDate)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }

}