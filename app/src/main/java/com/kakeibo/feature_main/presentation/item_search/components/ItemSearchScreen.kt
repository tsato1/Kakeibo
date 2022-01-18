package com.kakeibo.feature_main.presentation.item_search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.feature_main.presentation.item_search.ItemSearchEvent
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_search.ItemSearchViewModel
import com.kakeibo.feature_main.presentation.item_search.SearchCriterion
import com.kakeibo.feature_main.presentation.util.Screen
import com.kakeibo.ui.theme.LightCream
import kotlinx.coroutines.flow.collectLatest

@ExperimentalComposeUiApi
@Composable
fun ItemSearchScreen(
    navController: NavController,
    viewModel: ItemSearchViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()

    val openDialog = remember { mutableStateOf(false) }

    val defaultSearchCriteria = viewModel.defaultSearchCriteriaState.value
    val chosenSearchCriteria = viewModel.chosenSearchCriteriaState.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ItemSearchViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is ItemSearchViewModel.UiEvent.Search -> {
                    navController.navigate(
                        Screen.ItemListScreen.route + "?searchId=${event.searchId}"
                    )
                }
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    openDialog.value = true
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Criteria"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.onEvent(ItemSearchEvent.Search)
                }
            ) {
                Text(text = stringResource(id = R.string.search))
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
            ) {
                if (chosenSearchCriteria.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        text = stringResource(id = R.string.inst_tap_plus_to_add_criteria),
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(chosenSearchCriteria) { item ->
                        when (item) {
                            is SearchCriterion.TypeDateRange -> {
                                SearchCardDateRange(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center)
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(LightCream)
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(15.dp)
                                        ),
                                    viewModel
                                )
                            }
                            is SearchCriterion.TypeAmount -> {
                                SearchCardAmount(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(LightCream)
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(15.dp)
                                        ),
                                    viewModel
                                )
                            }
                            is SearchCriterion.TypeCategory -> {
                                SearchCardCategory(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(LightCream)
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(15.dp)
                                        ),
                                    viewModel
                                )
                            }
                            is SearchCriterion.TypeMemo -> {
                                SearchCardMemo(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(LightCream)
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(15.dp)
                                        ),
                                    viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(text = stringResource(id = R.string.add_search_criterion))
            },
            onDismissRequest = { openDialog.value = false },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(defaultSearchCriteria.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp, horizontal = 4.dp)
                                .clickable {
                                    val selectedType = defaultSearchCriteria[index]
                                    viewModel.onEvent(ItemSearchEvent.CriterionAdded(selectedType))
                                    openDialog.value = false
                                }
                        ) {
                            Text(text = defaultSearchCriteria[index].name)
                        }
                        Divider()
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { openDialog.value = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {}
        )
    }

}