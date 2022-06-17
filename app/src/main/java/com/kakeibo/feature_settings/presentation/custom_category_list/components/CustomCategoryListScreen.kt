package com.kakeibo.feature_settings.presentation.custom_category_list.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.presentation.components.BannerAds
import com.kakeibo.feature_settings.domain.util.CustomCategoryListOrder
import com.kakeibo.feature_settings.presentation.custom_category_list.CustomCategoryListEvent
import com.kakeibo.feature_settings.presentation.custom_category_list.CustomCategoryListViewModel
import com.kakeibo.feature_settings.presentation.util.Screen
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@Composable
fun CustomCategoryListScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navController: NavController,
    viewModel: CustomCategoryListViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val customCategoryListState = viewModel.customCategoryListState.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is CustomCategoryListViewModel.UiEvent.ShowToast -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.load()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val code = viewModel.getNewCode()
                    when {
                        code >= UtilCategory.CUSTOM_CATEGORY_CODE_START + UtilCategory.NUM_MAX_CUSTOM_CATEGORY -> {
                            Toast.makeText(
                                context,
                                R.string.err_reached_max_count,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                            navController.navigate(
                                Screen.CustomCategoryDetailScreen.route + "?categoryCode=$code"
                            )
                        }
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            IconButton(
                onClick = {
                    viewModel.onEvent(CustomCategoryListEvent.ToggleOrderSection)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort"
                )
            }
            AnimatedVisibility(
                visible = customCategoryListState.isOrderSectionVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OrderSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    order = customCategoryListState.listOrder,
                    onOrderChange = {
                        viewModel.onEvent(CustomCategoryListEvent.Reorder(it))
                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(customCategoryListState.customCategoryList) { categoryModel ->

                    val message = stringResource(id = R.string.msg_category_successfully_deleted)
                    val actionLabel = stringResource(id = R.string.undo)
                    val id = categoryModel._id

                    CustomCategoryListItem(
                        categoryModel = categoryModel,
                        onEditClick = {
                            navController.navigate(
                                Screen.CustomCategoryDetailScreen.route + "?categoryId=$id"
                            )
                        },
                        onDeleteClick = {
                            viewModel.onEvent(CustomCategoryListEvent.Delete(categoryModel))

                            scope.launch {
                                val result = scaffoldState.snackbarHostState.showSnackbar(
                                    message = message,
                                    actionLabel = actionLabel
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onEvent(CustomCategoryListEvent.Restore)
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            if (viewModel.kkbAppModelState.value.kkbAppModel.intVal2 == ConstKkbAppDB.AD_SHOW) {
                BannerAds(
                    adId = stringResource(id = R.string.main_banner_ad)
                )
            }
        }
    }
}

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    order: CustomCategoryListOrder = CustomCategoryListOrder.Name,
    onOrderChange: (CustomCategoryListOrder) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = stringResource(id = R.string.name),
                selected = order is CustomCategoryListOrder.Name,
                onSelect = { onOrderChange(CustomCategoryListOrder.Name) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = stringResource(id = R.string.date),
                selected = order is CustomCategoryListOrder.Code,
                onSelect = { onOrderChange(CustomCategoryListOrder.Code) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            DefaultRadioButton(
                text = stringResource(id = R.string.type),
                selected = order is CustomCategoryListOrder.Color,
                onSelect = { onOrderChange(CustomCategoryListOrder.Color) }
            )
        }
    }
}

@Composable
fun DefaultRadioButton(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colors.primary,
                unselectedColor = MaterialTheme.colors.onBackground
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1
        )
    }
}