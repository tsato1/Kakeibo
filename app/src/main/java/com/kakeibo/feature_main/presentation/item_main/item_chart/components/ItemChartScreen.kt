package com.kakeibo.feature_main.presentation.item_main.item_chart.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
//import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
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

    Scaffold(
        floatingActionButton = {
            if (viewModel.searchId.value == 0L) {
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
                    if (viewModel.searchId.value == 0L) {
                        detectDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > 200 -> { viewModel.plus(Calendar.MONTH, -1) }
                                    offsetX < -200 -> { viewModel.plus(Calendar.MONTH, 1) }
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
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.searchId.value != 0L) {
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
                        dateFormatIndex = viewModel.dateFormatIndexState.value,
                        viewModel = viewModel
                    )
                }
                BalanceSummaryRow(itemChartState)
                Spacer(modifier = Modifier.height(2.dp))
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
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
                                        slices = itemChartState.value.incomeList
                                            .mapIndexed { index, value ->
                                                PieChartData.Slice(
                                                    value.amount.toFloat(),
                                                    if (index < 10)
                                                        Color(Constants.CATEGORY_INCOME_COLORS[index].toColorInt())
                                                    else
                                                        MaterialTheme.colors.background
                                                )
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
                                                image = displayedItemModel.categoryImage,
                                                size = dimensionResource(id = R.dimen.category_list_row_height)
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
                                                    .background(
                                                        if (index < 10)
                                                            Color(Constants.CATEGORY_INCOME_COLORS[index].toColorInt())
                                                        else
                                                            MaterialTheme.colors.background
                                                    )
                                                    .aspectRatio(1f)
                                            )
                                            Text(
                                                modifier = Modifier.width(55.dp),
                                                text = "${
                                                    displayedItemModel.amount.toBigDecimal()
                                                        .times(BigDecimal(100))
                                                        .divide(
                                                            itemChartState.value.incomeTotal.toBigDecimal(),
                                                            RoundingMode.HALF_DOWN
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
                                        slices = itemChartState.value.expenseList
                                            .mapIndexed { index, value ->
                                                PieChartData.Slice(
                                                    value.amount.toFloat(),
                                                    if (index < 10)
                                                        Color(Constants.CATEGORY_EXPENSE_COLORS[index].toColorInt())
                                                    else
                                                        MaterialTheme.colors.background
                                                )
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
                                                image = displayedItemModel.categoryImage,
                                                size = dimensionResource(id = R.dimen.category_list_row_height)
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
                                                    .background(
                                                        if (index < 10)
                                                            Color(Constants.CATEGORY_EXPENSE_COLORS[index].toColorInt())
                                                        else
                                                            MaterialTheme.colors.background
                                                    )
                                                    .aspectRatio(1f)
                                            )
                                            Text(
                                                modifier = Modifier.width(55.dp),
                                                text = "${
                                                    displayedItemModel.amount.toBigDecimal()
                                                        .times(BigDecimal(100))
                                                        .divide(
                                                            itemChartState.value.expenseTotal.toBigDecimal(),
                                                            RoundingMode.HALF_DOWN
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
                    if (itemChartState.value.incomeList.isEmpty() && itemChartState.value.expenseList.isEmpty()) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.no_item_found),
                        )
                    }
                }
            }
            if (viewModel.kkbAppModelState.value.kkbAppModel.intVal2 == ConstKkbAppDB.AD_SHOW) {
//                BannerAds(
//                    modifier = Modifier.align(Alignment.BottomCenter),
//                    adId = stringResource(id = R.string.main_banner_ad)
//                )
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
                navController.navigate(Screen.ItemChartScreen.route +
                        "?searchId=${0L}/?focusDate=${Calendar.getInstance().toYMDString(UtilDate.DATE_FORMAT_DB)}/?focusItemId=${-1L}/?reload=${true}")
                viewModel.onEvent(ItemMainEvent.ExitSearchMode)
                openExitSearchDialog.value = false
            }
        )
    }

    if (openSearchDetailDialog.value) {
        SearchDetailDialog(
            onDismissRequest = { openSearchDetailDialog.value = false },
            onConfirmButtonClick = { openSearchDetailDialog.value = false },
            searchModel = viewModel.searchModel.value
        )
    }

    val openItemDetailDialog = rememberSaveable { mutableStateOf(false) }
    val openItemDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val clickedItem = remember {
        mutableStateOf(
            DisplayedItemModel(
                "0", "", "", 0, "", "", ""
            )
        )
    }

    if (openDetailListDialog.value) {
        DialogCard(
            onDismissRequest = { openDetailListDialog.value = false },
            title = clickedCategoryName.value,
            positiveButton = {
                OutlinedButton(
                    onClick = { openDetailListDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.close))
                }
            },
            content = {
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
                            val dropdownMenuExpanded = rememberSaveable { mutableStateOf(false) }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp, 6.dp)
                                    .combinedClickable(
                                        onClick = {
                                            openItemDetailDialog.value = true
                                            clickedItem.value = item
                                        },
                                        onLongClick = {
                                            dropdownMenuExpanded.value = true
                                            clickedItem.value = item
                                        }
                                    ),
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
                            /* dropdown menu will open when an item is long clicked */
                            DropdownMenu(
                                expanded = dropdownMenuExpanded.value,
                                onDismissRequest = {
                                    dropdownMenuExpanded.value = false
                                }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        dropdownMenuExpanded.value = false
                                        openItemDeleteDialog.value = true
                                    }
                                ) {
                                    Text(text = stringResource(id = R.string.delete))
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    if (openItemDetailDialog.value) {
        ItemDetailDialog(
            item = clickedItem,
            onDismissRequest = { openItemDetailDialog.value = false },
            onEditButtonClick = {
                navController.navigate(
                    Screen.ItemDetailScreen.route + "?itemId=${clickedItem.value.id}"
                )
                openItemDetailDialog.value = false
            }
        )
    }

    if (openItemDeleteDialog.value) {
        ItemDeleteDialog(
            item = clickedItem.value,
            onDismissRequest = { openItemDeleteDialog.value = false },
            onDeleteButtonClicked = {
                openItemDeleteDialog.value = false
                openDetailListDialog.value = false
                viewModel.onEvent(ItemMainEvent.DeleteItem(clickedItem.value))
            }
        )
    }
}