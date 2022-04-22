package com.kakeibo.feature_settings.presentation.category_rearrange.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kakeibo.R
import com.kakeibo.core.presentation.components.GridCategoryItem
import com.kakeibo.feature_settings.presentation.category_rearrange.CategoryRearrangeEvent
import com.kakeibo.feature_settings.presentation.category_rearrange.CategoryRearrangeViewModel
import com.kakeibo.feature_settings.presentation.category_reorder.CategoryReorderActivity
import com.kakeibo.ui.theme.LightCream
import kotlinx.coroutines.flow.collectLatest

@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CategoryRearrangeScreen(
    navController: NavController,
    viewModel: CategoryRearrangeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    val gridListState = rememberLazyListState()
    val openSaveDialog = remember { mutableStateOf(false) }

    val categoryRearrangeState = viewModel.categoryRearrangeState.value
    val finalCategoryList = viewModel.categoryRearrangeState.value.finalCategoryList

    val showRemoveIconListState = remember { mutableStateListOf(false) }
    for (index in 0..categoryRearrangeState.displayedCategoryList.size)
        showRemoveIconListState.add(false)
    val showAddIconListState = remember { mutableStateListOf(false) }
    for (index in 0..categoryRearrangeState.nonDisplayedCategoryList.size)
        showAddIconListState.add(false)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CategoryRearrangeViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        event.message.asString(context)
                    )
                }
                is CategoryRearrangeViewModel.UiEvent.SaveAndReorder -> { // Save and Reorder
                    Toast.makeText(context, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                    context.startActivity(Intent(context, CategoryReorderActivity::class.java))
                }
                is CategoryRearrangeViewModel.UiEvent.SaveWithoutReorder -> { // Save without Reorder
                    Toast.makeText(context, R.string.msg_change_successfully_saved, Toast.LENGTH_LONG).show()
                    navController.navigateUp()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = 3,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            when (page) {
                0 -> {
                    /**************************************************************************/
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(LightCream)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = stringResource(id = R.string.hide_categories)
                        )
                        LazyVerticalGrid(
                            cells = GridCells.Fixed(
                                count = viewModel.numColumns
                            ),
                            contentPadding = PaddingValues(
                                start = 6.dp,
                                top = 8.dp,
                                end = 6.dp,
                                bottom = 8.dp
                            ),
                            content = {
                                items(categoryRearrangeState.displayedCategoryList.size) { index ->
                                    val category =
                                        categoryRearrangeState.displayedCategoryList[index]

                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                    ) {
                                        GridCategoryItem(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .fillMaxWidth(),
                                            categoryModel = category.toCategoryEntity()
                                                .toDisplayedCategoryModel(),
                                            onItemClick = {
                                                showRemoveIconListState[index] =
                                                    !showRemoveIconListState[index]

                                                if (showRemoveIconListState[index]) {
                                                    viewModel.onEvent(
                                                        CategoryRearrangeEvent.Remove(
                                                            category
                                                        )
                                                    )
                                                } else {
                                                    viewModel.onEvent(
                                                        CategoryRearrangeEvent.Add(
                                                            category
                                                        )
                                                    )
                                                }
                                            },
                                            onItemLongClick = { }
                                        )

                                        if (showRemoveIconListState[index]) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                tint = Color.Black,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                            },
                            state = gridListState
                        )
                    }
                }
                1 -> {
                    /**************************************************************************/
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(LightCream)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = stringResource(id = R.string.display_categories)
                        )
                        Text(
                            text = stringResource(id = R.string.remaining_spots_colon)
                        )
                        LazyVerticalGrid(
                            cells = GridCells.Fixed(
                                count = viewModel.numColumns
                            ),
                            contentPadding = PaddingValues(
                                start = 6.dp,
                                top = 8.dp,
                                end = 6.dp,
                                bottom = 8.dp
                            ),
                            content = {
                                items(categoryRearrangeState.nonDisplayedCategoryList.size) { index ->
                                    val category =
                                        categoryRearrangeState.nonDisplayedCategoryList[index]

                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        GridCategoryItem(
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .fillMaxWidth(),
                                            categoryModel = category.toCategoryEntity()
                                                .toDisplayedCategoryModel(),
                                            onItemClick = {
                                                showAddIconListState[index] =
                                                    !showAddIconListState[index]

                                                if (showAddIconListState[index]) {
                                                    viewModel.onEvent(
                                                        CategoryRearrangeEvent.Add(
                                                            category
                                                        )
                                                    )
                                                } else {
                                                    viewModel.onEvent(
                                                        CategoryRearrangeEvent.Remove(
                                                            category
                                                        )
                                                    )
                                                }
                                            },
                                            onItemLongClick = { }
                                        )

                                        if (showAddIconListState[index]) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                tint = Color.Black,
                                                contentDescription = ""
                                            )
                                        }
                                    }
                                }
                            },
                            state = gridListState
                        )
                    }
                }
                2 -> {
                    /**************************************************************************/
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(LightCream)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Here are the Categories to be displayed on INPUT screen"
                        )
                        LazyVerticalGrid(
                            cells = GridCells.Fixed(
                                count = viewModel.numColumns
                            ),
                            contentPadding = PaddingValues(
                                start = 6.dp,
                                top = 8.dp,
                                end = 6.dp,
                                bottom = 8.dp
                            ),
                            content = {
                                items(finalCategoryList.size) { index ->
                                    val category = finalCategoryList[index]

                                    GridCategoryItem(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .fillMaxWidth(),
                                        categoryModel = category.toCategoryEntity()
                                            .toDisplayedCategoryModel(),
                                        onItemClick = { },
                                        onItemLongClick = { }
                                    )
                                }
                            },
                            state = gridListState
                        )
                        AnimatedVisibility(visible = pagerState.currentPage == 2) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .padding(20.dp, 0.dp)
                                    .clip(RoundedCornerShape(15.dp)),
                                onClick = { openSaveDialog.value = true }
                            ) {
                                Text(text = stringResource(R.string.next))
                            }
                        }
                    }
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp),
        )
    }

    if (openSaveDialog.value) {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = "Next Step"
                )
            },
            onDismissRequest = {
                openSaveDialog.value = false
            },
            text = {
                Text(
                    text = "You can choose to reorder the Category icons on INPUT screen after saving this",
                    color = Color.Black
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { openSaveDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        openSaveDialog.value = false
                        viewModel.onEvent(CategoryRearrangeEvent.SaveWithoutReorder)
                    }
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
                OutlinedButton(
                    onClick = {
                        openSaveDialog.value = false
                        viewModel.onEvent(CategoryRearrangeEvent.SaveAndReorder)
                    }
                ) {
                    Text(text = stringResource(id = R.string.reorder_categories))
                }
            }
        )
    }

}