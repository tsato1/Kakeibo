package com.kakeibo.feature_settings.presentation.settings_list.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kakeibo.R
import com.kakeibo.feature_settings.presentation.util.Screen
import com.kakeibo.feature_settings.presentation.settings_list.SettingsListEvent
import com.kakeibo.feature_settings.presentation.settings_list.SettingsListViewModel
import com.kakeibo.feature_settings.presentation.category_reorder.CategoryReorderActivity

@Composable
fun SettingsListScreen(
    navController: NavController,
    viewModel: SettingsListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { scrollState.animateScrollTo(0) }

    viewModel.setSharedPreferencesStates()
    val keyDateFormatIndexState = viewModel.keyDateFormatIndexState
    val keyFractionDigitsIndexState = viewModel.keyFractionDigitsIndexState
    val keyNumColumnsIndexState = viewModel.keyNumColumnsIndexState

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
                            navController.navigate(Screen.CustomCategoryListScreen.route)
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
                            navController.navigate(Screen.CategoryRearrangeScreen.route)
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
//                            navController.navigate(Screen.RearrangeCategoriesScreen.route) // future todo
                            context.startActivity(Intent(context, CategoryReorderActivity::class.java))
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
                    modifier = Modifier
                        .clickable {

                        }
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_all_items),
                        modifier = Modifier.padding(16.dp)
                    )
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
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .clip(RoundedCornerShape(15.dp)),
            title = {
                Text(
                    text = stringResource(id = titleResourceId),
                    style = MaterialTheme.typography.h4
                )
            },
            onDismissRequest = { openDialog.value = false },
            buttons = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        viewModel.onEvent(event, 0)
                        openDialog.value = false
                    }
                ) {
                    if (indexState.value == 0)
                        Icon(imageVector = Icons.Default.RadioButtonChecked, contentDescription = null)
                    else
                        Icon(imageVector = Icons.Default.RadioButtonUnchecked, contentDescription = null)
                    Text(
                        text = stringArrayResource(id = arrayResourceId)[0],
                        modifier = Modifier.padding(8.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        viewModel.onEvent(event, 1)
                        openDialog.value = false
                    }
                ) {
                    if (indexState.value == 1)
                        Icon(imageVector = Icons.Default.RadioButtonChecked, contentDescription = null)
                    else
                        Icon(imageVector = Icons.Default.RadioButtonUnchecked, contentDescription = null)
                    Text(
                        text = stringArrayResource(id = arrayResourceId)[1],
                        modifier = Modifier.padding(8.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    onClick = {
                        viewModel.onEvent(event, 2)
                        openDialog.value = false
                    }
                ) {
                    if (indexState.value == 2)
                        Icon(imageVector = Icons.Default.RadioButtonChecked, contentDescription = null)
                    else
                        Icon(imageVector = Icons.Default.RadioButtonUnchecked, contentDescription = null)
                    Text(
                        text = stringArrayResource(id = arrayResourceId)[2],
                        modifier = Modifier.padding(8.dp, 0.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
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