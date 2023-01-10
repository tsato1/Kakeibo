package com.kakeibo.feature_main.presentation.item_main.item_list.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
//import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toCalendar
import com.kakeibo.util.UtilDate.toYMDString
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ItemListScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navController: NavController,
    viewModel: ItemMainViewModel,
    searchId: Long,
    focusDate: String,
    focusItemId: String,
    reload: Boolean
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val openSearchDetailDialog = remember { mutableStateOf(false) }
    val openExitSearchDialog = remember { mutableStateOf(false) }

    val itemListState by viewModel.expandableItemListState.collectAsState()

    LaunchedEffect(viewModel.eventFlow, scaffoldState.snackbarHostState) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemMainViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
                /* UiEvent.LoadingCompleted is implemented in CollapsableLazyColumn */
                else -> Unit
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (searchId != 0L || reload) {
                    viewModel.onEvent(ItemMainEvent.LoadItems(searchId, focusDate.toCalendar(), focusItemId))
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                                    offsetX > 200 -> {
                                        viewModel.plus(Calendar.MONTH, -1)
                                    }
                                    offsetX < -200 -> {
                                        viewModel.plus(Calendar.MONTH, 1)
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
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
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
                BalanceSummaryRow(itemChartState = viewModel.itemChartState)
                Spacer(modifier = Modifier.height(2.dp))
                Divider()
                CollapsableLazyColumn(
                    navController = navController,
                    sections = itemListState.expandableItemList,
                    dateFormatIndex = viewModel.dateFormatIndexState.value,
                    fractionDigits = viewModel.fractionDigitsIndexState.value,
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel
                )
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
               navController.navigate(Screen.ItemListScreen.route +
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

}