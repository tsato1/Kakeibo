package com.kakeibo.feature_main.presentation.item_search.components

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun ItemSearchScreen(
    navController: NavController,
    viewModel: ItemSearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

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
        scaffoldState = scaffoldState,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
            ) {
                if (chosenSearchCriteria.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = stringResource(id = R.string.inst_tap_plus_to_add_criteria),
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(chosenSearchCriteria) { item ->
                        var isDeleted by remember { mutableStateOf(false) }
                        val dismissState = rememberDismissState(
                            confirmStateChange = {
                                if (it == DismissValue.DismissedToStart)
                                    isDeleted = !isDeleted

                                if (isDeleted) {
                                    viewModel.onEvent(ItemSearchEvent.CriterionRemoved(item))
                                    coroutineScope.launch {
                                        val result = scaffoldState.snackbarHostState.showSnackbar(
                                            "${item.name} discarded.",
                                            "Undo"
                                        )
                                        when (result) {
                                            SnackbarResult.Dismissed -> {
                                            }
                                            SnackbarResult.ActionPerformed -> {
                                                viewModel.onEvent(ItemSearchEvent.CriterionAdded(item))
                                                isDeleted = false
                                            }
                                        }
                                    }
                                }

                                it != DismissValue.DismissedToStart
                            }
                        )

                        SwipeToDismiss(
                            state = dismissState,
                            modifier = Modifier.padding(vertical = 1.dp),
                            directions = setOf(DismissDirection.EndToStart),
                            dismissThresholds = { direction ->
                                FractionalThreshold(
                                    if (direction == DismissDirection.StartToEnd) 0.75f else 0.75f
                                )
                            },
                            background = {
                                val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.LightGray
                                        else -> Color.Red
                                    }
                                )
                                val alignment = when (direction) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    DismissDirection.EndToStart -> Alignment.CenterEnd
                                }
                                val icon = when (direction) {
                                    DismissDirection.StartToEnd -> Icons.Default.Delete
                                    DismissDirection.EndToStart -> Icons.Default.Delete
                                }
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == DismissValue.Default)
                                        0.75f
                                    else
                                        1f
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(vertical = 14.dp, horizontal = 14.dp)
                                        .clip(RoundedCornerShape(15.dp)),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = "Icon",
                                        modifier = Modifier.scale(scale)
                                    )
                                }
                            },
                            dismissContent = {
                                if (!isDeleted) {
                                    SearchCard(
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
                                        viewModel,
                                        item
                                    )
                                }
                            }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    if (chosenSearchCriteria.isEmpty()) {
                        Toast.makeText(
                            context, R.string.err_no_search_criteria_found, Toast.LENGTH_LONG
                        ).show()
                    }
                    else {
                        viewModel.onEvent(ItemSearchEvent.Search)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.search))
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                    Text(text = stringResource(id = R.string.add_search_criterion))
                }
            },
            onDismissRequest = { openDialog.value = false },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchCard(
    modifier: Modifier,
    viewModel: ItemSearchViewModel,
    criterion: SearchCriterion
) {
    when (criterion) {
        is SearchCriterion.TypeDateRange -> {
            SearchCardDateRange(
                modifier = modifier,
                viewModel
            )
        }
        is SearchCriterion.TypeAmount -> {
            SearchCardAmount(
                modifier = modifier,
                viewModel
            )
        }
        is SearchCriterion.TypeCategory -> {
            SearchCardCategory(
                modifier = modifier,
                viewModel
            )
        }
        is SearchCriterion.TypeMemo -> {
            SearchCardMemo(
                modifier = modifier,
                viewModel
            )
        }
    }
}