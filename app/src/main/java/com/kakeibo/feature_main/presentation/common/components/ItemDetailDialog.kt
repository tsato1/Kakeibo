package com.kakeibo.feature_main.presentation.common.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kakeibo.R
import com.kakeibo.core.presentation.components.CategoryIcon
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import com.kakeibo.feature_main.domain.models.DisplayedItemModel

@Composable
fun ItemDetailDialog(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    navController: NavController,
    item: State<DisplayedItemModel>,
    onDismissRequest: () -> Unit,
    onEditButtonClick: () -> Unit
) {
//    navController.currentBackStackEntry//KOKONIHA KONAI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        ?.savedStateHandle
//        ?.getLiveData<Int>("asdf")
////        ?.getLiveData<DisplayedCategoryModel>("categoryModel")
//        ?.observe(lifecycleOwner) {
//            Log.d("asdf", "oioi in detailDialog: " + it)
//        }
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    Log.d("asdf", "itemDetailDialog!! "+currentRoute)

    DialogCard(
        onDismissRequest = { onDismissRequest() },
        title = stringResource(id = R.string.item_detail),
        content = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.event_date_colon))
                    Text(text = item.value.eventDate)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.category_colon))
                    Log.d("asdf", "itemDetailDialog "+item.value.categoryCode)
//                    CategoryIcon(
//                        code = item.value.categoryCode,
//                        drawable = item.value.categoryDrawable,
//                        image = item.value.categoryImage,
//                        size = 30.dp
//                    )
                    Text(text = item.value.categoryName)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.amount_colon))
                    Text(text = item.value.amount)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.memo_colon))
                    Text(text = item.value.memo)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.updated_on_colon))
                    Text(text = item.value.updateDate)
                }
            }
        },
        negativeButton = {
            OutlinedButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        positiveButton = {
            OutlinedButton(
                onClick = { onEditButtonClick() }
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        }
    )
}