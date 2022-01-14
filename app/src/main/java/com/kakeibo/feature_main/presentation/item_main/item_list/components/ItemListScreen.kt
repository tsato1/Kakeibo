package com.kakeibo.feature_main.presentation.item_main.item_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakeibo.feature_main.presentation.common.components.DatePickerRow
import com.kakeibo.feature_main.presentation.common.components.DateType
import com.kakeibo.feature_main.presentation.item_main.ItemMainViewModel
import com.kakeibo.feature_main.presentation.item_main.components.BottomBar
import com.kakeibo.feature_main.presentation.util.Screen

@Composable
fun ItemListScreen(
    navController: NavController,
    viewModel: ItemMainViewModel
) {
    val itemListState = viewModel.expandableItemListState.value
    val scaffoldState = rememberScaffoldState()

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
            DatePickerRow(
                context = LocalContext.current,
                type = DateType.YM,
                dateFormatIndex = viewModel.dateFormatIndex,
                onTextLayout = { }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CollapsableLazyColumn(
                navController = navController,
                sections = itemListState.expandableItemList,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}