package com.kakeibo.feature_main.presentation.item_main.item_list.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.util.Screen

@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: ItemMainViewModel,
    searchId: Long = -1L
) {
    val scaffoldState = rememberScaffoldState()

    val openSearchDetailDialog = remember { mutableStateOf(false) }
    val openExitSearchDialog = remember { mutableStateOf(false) }

    val itemListState = viewModel.expandableItemListState.value
    val searchIdState = viewModel.searchId
    val searchModel = viewModel.searchModel.value

    LaunchedEffect(Unit) {
        Log.d("asdf", "launchedEffect in list searchId="+searchId)
        if (searchId != -1L) {
            viewModel.onEvent(ItemMainEvent.LoadItems(searchId))
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
                Spacer(modifier = Modifier.height(8.dp))
                CollapsableLazyColumn(
                    navController = navController,
                    sections = itemListState.expandableItemList,
                    modifier = Modifier.fillMaxSize()
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