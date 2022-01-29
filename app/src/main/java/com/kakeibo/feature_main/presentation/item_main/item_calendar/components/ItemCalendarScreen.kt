package com.kakeibo.feature_main.presentation.item_main.item_calendar.components

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.feature_main.presentation.common.components.*
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.item_main.ItemMainEvent
import com.kakeibo.feature_main.presentation.util.Screen

@Composable
fun ItemCalendarScreen(
    navController: NavController,
    viewModel: ItemMainViewModel
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val scaffoldState = rememberScaffoldState()

    val openSearchDetailDialog = remember { mutableStateOf(false) }
    val openExitSearchDialogState = viewModel.openExitSearchDialogState

    val searchIdState = viewModel.searchId



    LaunchedEffect(Unit) {
        Log.d("asdf", "launchedEffect CALENDAR searchId="+searchIdState.value)
        if (searchIdState.value != -1L) {
            viewModel.onEvent(ItemMainEvent.LoadItems(searchIdState.value))
        }
    }

    Scaffold(
        floatingActionButton = {
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
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        bottomBar = { BottomBar(navController = navController) },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (searchIdState.value != -1L) {
                SearchModeTopRow(
                    onCloseButtonClick = {
                        viewModel.onEvent(ItemMainEvent.OpenExitSearchDialog(true))
                    },
                    onTextButtonClick = {
                        openSearchDetailDialog.value = true
                    }
                )
            }
            else {
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

        }
    }

    if (openExitSearchDialogState.value) {
        ExitSearchAlertDialog(
            onDismissRequest = { openSearchDetailDialog.value = false },
            onDismissButtonClick = {
                viewModel.onEvent(ItemMainEvent.OpenExitSearchDialog(false))
            },
            onConfirmButtonClick = {
                viewModel.onEvent(ItemMainEvent.ExitSearchMode)
                viewModel.onEvent(ItemMainEvent.OpenExitSearchDialog(false))
            }
        )
    }

    if (openSearchDetailDialog.value) {
        SearchDetailAlertDialog(
            onDismissRequest = { openSearchDetailDialog.value = false },
            onConfirmButtonClick = { openSearchDetailDialog.value = false }
        )
    }
}