package com.kakeibo.feature_settings.presentation.settings_list.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.core.data.constants.ConstKkbAppDB
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_settings.presentation.util.Screen
import com.kakeibo.feature_settings.presentation.settings_list.SettingsListEvent
import com.kakeibo.feature_settings.presentation.settings_list.SettingsListViewModel
import com.kakeibo.feature_settings.presentation.category_reorder.CategoryReorderActivity
import com.kakeibo.ui.theme.MatchaGreen
import com.kakeibo.ui.theme.dimens
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsListScreen(
    navController: NavController,
    viewModel: SettingsListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) { scrollState.animateScrollTo(0) }

    viewModel.setSharedPreferencesStates()
    val keyDateFormatIndexState = viewModel.keyDateFormatIndexState
    val keyFractionDigitsIndexState = viewModel.keyFractionDigitsIndexState
    val keyNumColumnsIndexState = viewModel.keyNumColumnsIndexState

    val kkbAppValues = viewModel.kkbAppState.value

    val openDeleteAllItemsDialog = remember { mutableStateOf(false) }
    val openConfirmAdsDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SettingsListViewModel.UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(event.stringId))
                }
                is SettingsListViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.stringId, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "General",
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.general),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                SettingsListItem(
                    titleResourceId = R.string.date_format,
                    arrayResourceId = R.array.pref_list_date_format,
                    indexState = keyDateFormatIndexState,
                    event = SettingsListEvent.DateFormatChanged,
                    viewModel
                )
                SettingsListItem(
                    titleResourceId = R.string.fraction_digits,
                    arrayResourceId = R.array.pref_list_fraction_digits,
                    indexState = keyFractionDigitsIndexState,
                    event = SettingsListEvent.FractionDigitsChanged,
                    viewModel
                )
                SettingsListItem(
                    titleResourceId = R.string.num_categories_per_row,
                    arrayResourceId = R.array.pref_list_num_columns,
                    indexState = keyNumColumnsIndexState,
                    event = SettingsListEvent.NumColumnsChanged,
                    viewModel
                )
            }
        }
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Category",
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.category),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (kkbAppValues.intVal2 == ConstKkbAppDB.AD_SHOW) {
                                navController.navigate(Screen.CustomCategoryListScreen.route)
                            } else {
                                openConfirmAdsDialog.value = true
                            }
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.custom_categories),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (kkbAppValues.intVal2 == ConstKkbAppDB.AD_SHOW) {
                                navController.navigate(Screen.CategoryRearrangeScreen.route)
                            } else {
                                openConfirmAdsDialog.value = true
                            }
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.hide_display_reorder_categories),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (kkbAppValues.intVal2 == ConstKkbAppDB.AD_SHOW) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        CategoryReorderActivity::class.java
                                    )
                                )
                            } else {
                                openConfirmAdsDialog.value = true
                            }
                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.reorder_categories),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.PermDataSetting,
                contentDescription = "In-App Data",
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(id = R.string.in_app_data),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.clickable { openDeleteAllItemsDialog.value = true }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_all_items),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    if (openConfirmAdsDialog.value) {
        Dialog(
            onDismissRequest = { openConfirmAdsDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.dialogHeightDefault)
                    .clip(RoundedCornerShape(MaterialTheme.dimens.dialogRoundedCorner))
                    .background(MaterialTheme.colors.background)
            ) {
                val openConfirmAdsDialog2 = remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            MaterialTheme.dimens.dialogTitlePaddingHorizontal,
                            MaterialTheme.dimens.dialogTitlePaddingVertical
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                    Text(text = stringResource(id = R.string.category_management))
                }
                Divider()
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(MaterialTheme.dimens.dialogPadding),
                    text = stringResource(id = R.string.quest_do_you_want_to_manage_categories)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.dialogPadding)
                        .align(Alignment.End),
                    onClick = { openConfirmAdsDialog2.value = true }
                ) {
                    Text(text = stringResource(id = R.string.yes))
                }

                if (openConfirmAdsDialog2.value) {
                    Dialog(
                        onDismissRequest = {
                            openConfirmAdsDialog.value = false
                            openConfirmAdsDialog2.value = false
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.dimens.dialogHeightShort)
                                .clip(RoundedCornerShape(MaterialTheme.dimens.dialogRoundedCorner))
                                .background(MaterialTheme.colors.background)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        MaterialTheme.dimens.dialogTitlePaddingHorizontal,
                                        MaterialTheme.dimens.dialogTitlePaddingVertical
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                                Text(text = stringResource(id = R.string.warning))
                            }
                            Divider()
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                modifier = Modifier.padding(MaterialTheme.dimens.dialogPadding),
                                text = stringResource(id = R.string.quest_irreversible_operation_do_you_want_to_proceed)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                modifier = Modifier
                                    .padding(MaterialTheme.dimens.dialogPadding)
                                    .align(Alignment.End),
                                onClick = {
                                    viewModel.onEvent(SettingsListEvent.ShowAds, -1)
                                    openConfirmAdsDialog.value = false
                                    openConfirmAdsDialog2.value = false
                                }
                            ) {
                                Text(text = stringResource(id = R.string.yes))
                            }
                        }
                    }
                }
            }
        }
    }

    if (openDeleteAllItemsDialog.value) {
        Dialog(
            onDismissRequest = { openDeleteAllItemsDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.dialogHeightDefault)
                    .clip(RoundedCornerShape(MaterialTheme.dimens.dialogRoundedCorner))
                    .background(MaterialTheme.colors.background)
            ) {
                val openConfirmDialog = remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            MaterialTheme.dimens.dialogTitlePaddingHorizontal,
                            MaterialTheme.dimens.dialogTitlePaddingVertical
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                    Text(text = stringResource(id = R.string.delete_all_items))
                }
                Divider()
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(MaterialTheme.dimens.dialogPadding),
                    text = stringResource(id = R.string.desc_delete_all_items)
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.dialogPadding)
                        .align(Alignment.End),
                    onClick = { openConfirmDialog.value = true }
                ) {
                    Text(text = stringResource(id = R.string.yes))
                }

                if (openConfirmDialog.value) {
                    Dialog(
                        onDismissRequest = {
                            openConfirmDialog.value = false
                            openDeleteAllItemsDialog.value = false
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.dimens.dialogHeightShort)
                                .clip(RoundedCornerShape(MaterialTheme.dimens.dialogRoundedCorner))
                                .background(MaterialTheme.colors.background)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        MaterialTheme.dimens.dialogTitlePaddingHorizontal,
                                        MaterialTheme.dimens.dialogTitlePaddingVertical
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(painter = painterResource(id = R.mipmap.ic_mikan), contentDescription = "")
                                Text(text = stringResource(id = R.string.delete_all_items))
                            }
                            Divider()
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                modifier = Modifier.padding(MaterialTheme.dimens.dialogPadding),
                                text = stringResource(id = R.string.warn_delete_all_items)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                modifier = Modifier
                                    .padding(MaterialTheme.dimens.dialogPadding)
                                    .align(Alignment.End),
                                onClick = {
                                    viewModel.onEvent(SettingsListEvent.DeleteAllItems, -1)
                                    openConfirmDialog.value = false
                                    openDeleteAllItemsDialog.value = false
                                }
                            ) {
                                Text(text = stringResource(id = R.string.yes))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsListItem(
    titleResourceId: Int,
    arrayResourceId: Int,
    indexState: State<Int>,
    event: SettingsListEvent,
    viewModel: SettingsListViewModel
) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        DialogCard(
            onDismissRequest = { openDialog.value = false },
            title = stringResource(id = titleResourceId),
            content = {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.onEvent(event, 0)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.background),
                        elevation = null,
                        border = null
                    ) {
                        if (indexState.value == 0)
                            Icon(imageVector = Icons.Default.RadioButtonChecked, tint = MatchaGreen, contentDescription = null)
                        else
                            Icon(imageVector = Icons.Default.RadioButtonUnchecked, tint = MatchaGreen, contentDescription = null)
                        Text(
                            text = stringArrayResource(id = arrayResourceId)[0],
                            modifier = Modifier.padding(8.dp, 0.dp),
                            color = Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.onEvent(event, 1)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.background),
                        elevation = null,
                        border = null
                    ) {
                        if (indexState.value == 1)
                            Icon(imageVector = Icons.Default.RadioButtonChecked, tint = MatchaGreen, contentDescription = null)
                        else
                            Icon(imageVector = Icons.Default.RadioButtonUnchecked, tint = MatchaGreen, contentDescription = null)
                        Text(
                            text = stringArrayResource(id = arrayResourceId)[1],
                            modifier = Modifier.padding(8.dp, 0.dp),
                            color = Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.onEvent(event, 2)
                            openDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.background),
                        elevation = null,
                        border = null
                    ) {
                        if (indexState.value == 2)
                            Icon(imageVector = Icons.Default.RadioButtonChecked, tint = MatchaGreen, contentDescription = null)
                        else
                            Icon(imageVector = Icons.Default.RadioButtonUnchecked, tint = MatchaGreen, contentDescription = null)
                        Text(
                            text = stringArrayResource(id = arrayResourceId)[2],
                            modifier = Modifier.padding(8.dp, 0.dp),
                            color = Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
            .clickable {
                openDialog.value = true
            },
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = stringResource(id = titleResourceId),
        )
        Text(
            text = stringArrayResource(id = arrayResourceId)[indexState.value],
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.body2
        )
    }
}