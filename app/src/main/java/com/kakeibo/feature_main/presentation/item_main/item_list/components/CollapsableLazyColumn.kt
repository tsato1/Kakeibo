package com.kakeibo.feature_main.presentation.item_main.item_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.R
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.presentation.common.components.ItemDeleteDialog
import com.kakeibo.feature_main.presentation.common.components.ItemDetailDialog
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItem
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toCalendar
import com.kakeibo.util.UtilDate.toYMDWString
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollapsableLazyColumn(
    navController: NavController,
    sections: List<ExpandableItem>,
    dateFormatIndex: Int,
    fractionDigits: Int,
    modifier: Modifier,
    viewModel: ItemMainViewModel
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }

    val openItemDetailDialog = rememberSaveable { mutableStateOf(false) }
    val openItemDeleteDialog = rememberSaveable { mutableStateOf(false) }
    val clickedItem = remember {
        mutableStateOf(
            DisplayedItemModel(
                0L, "", "", 0, "", "", "", ""
            )
        )
    }

    LaunchedEffect(viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemMainViewModel.UiEvent.LoadingCompleted -> {
                    if (viewModel.focusItemIdState.value != -1L) {
                        listState.animateScrollToItem(viewModel.focusItemIdState.value.toInt())
                    }
                }
                /* UiEvent.ShowSnackbar is implemented in CollapsableLazyColumn */
                else -> Unit
            }
        }
    }

    if (sections.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.no_item_found)
            )
        }
    }
    else {
        LazyColumn(modifier = modifier, listState) {
            sections.forEachIndexed { i, expandableItem ->
                if (expandableItem.parent.scrollTo != -1) {
                    collapsedState[i] = false
                    expandableItem.parent.scrollTo = -1
                }

                val collapsed = collapsedState[i]

                item(key = "header_$i") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp, 4.dp)
                            .clickable {
                                collapsedState[i] = !collapsed
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.run {
                                if (collapsed)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
                            contentDescription = "",
                            tint = Color.LightGray,
                        )
                        Text(
                            text = expandableItem.parent.date.toCalendar()
                                .toYMDWString(UtilDate.DATE_FORMATS[dateFormatIndex], context),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Column(
                            modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "+",
                                style = TextStyle(
                                    color = Color.Blue
                                )
                            )
                        }
                        Text(text = expandableItem.parent.income.format(fractionDigits))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "-",
                                style = TextStyle(
                                    color = Color.Red
                                )
                            )
                        }
                        Text(text = expandableItem.parent.expense.format(fractionDigits))
                    }
                    Divider()
                }
                if (!collapsed) {
                    items(expandableItem.children) { child ->
                        val dropdownMenuExpanded = rememberSaveable { mutableStateOf(false) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp, 2.dp, 4.dp, 2.dp)
                                .combinedClickable(
                                    onClick = {
                                        openItemDetailDialog.value = true
                                        clickedItem.value = child
                                    },
                                    onLongClick = {
                                        dropdownMenuExpanded.value = true
                                        clickedItem.value = child
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIcon(
                                code = child.categoryCode,
                                drawable = child.categoryDrawable,
                                image = child.categoryImage
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp, 2.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = child.categoryName
                                )
                                Text(
                                    text = child.memo
                                )
                            }
                            when (child.categoryColor) {
                                UtilCategory.CATEGORY_COLOR_INCOME -> {
                                    Column(
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "+",
                                            style = TextStyle(
                                                color = Color.Blue
                                            )
                                        )
                                    }
                                }
                                UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                                    Column(
                                        modifier = Modifier.width(dimensionResource(id = R.dimen.plus_minus_size)),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "-",
                                            style = TextStyle(
                                                color = Color.Red
                                            )
                                        )
                                    }
                                }
                                else -> { }
                            }
                            Text(text = child.amount)
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
                        Divider()
                    }
                }
            }
        }
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
                viewModel.onEvent(ItemMainEvent.DeleteItem(clickedItem.value))
            }
        )
    }
}