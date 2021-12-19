package com.kakeibo.feature_settings.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.item_input.GridCategoryItem
import com.kakeibo.feature_settings.presentation.category_rearrange.presentation.CategoryRearrangeViewModel
import com.kakeibo.ui.theme.LightCream

@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun CategoryRearrangeScreen(
    navController: NavController,
    viewModel: CategoryRearrangeViewModel = hiltViewModel()
) {
    val gridListState = rememberLazyListState()

    val openSaveDialog = remember { mutableStateOf(false) }

    val categoryRearrangeState = viewModel.categoryRearrangeState.value

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
                    LazyVerticalGrid(
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
                        cells = GridCells.Fixed(
                            count = viewModel.appPreferences.getNumColumns()
                        ),
                        contentPadding = PaddingValues(
                            start = 6.dp,
                            top = 8.dp,
                            end = 6.dp,
                            bottom = 8.dp
                        ),
                        content = {
                            items(categoryRearrangeState.displayedCategoryList.size) { index ->
                                val category = categoryRearrangeState.displayedCategoryList[index]

                                GridCategoryItem(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    displayedCategory = category,
                                    onItemClick = {
                                        // todo show tickmark
                                    },
                                    onItemLongClick = {}
                                )
                            }
                        },
                        state = gridListState
                    )
                }
                1 -> {

                }
                2 -> {
                    AnimatedVisibility(visible = pagerState.currentPage == 1) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(20.dp, 0.dp)
                                .clip(RoundedCornerShape(15.dp)),
                            onClick = { openSaveDialog.value = true }
                        ) {
                            Text(text = stringResource(R.string.save))
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
//                    Icon() //todo load from mipmap
            },
            onDismissRequest = {
                openSaveDialog.value = false
            },
            text = {
                Text(
                    text = stringResource(id = R.string.quest_determine_category_order),
                    color = Color.Black
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { openSaveDialog.value = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        openSaveDialog.value = false
                        viewModel.onEvent()
                    }
                ) {
                    Text(text = stringResource(R.string.save))
                }
                OutlinedButton(
                    onClick = {
                        openSaveDialog.value = false
                        viewModel.onEvent()
                    }
                ) {
                    Text(text = stringResource(R.string.reorder_categories))
                }
            }
        )
    }

}