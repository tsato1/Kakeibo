package com.kakeibo.feature_settings.presentation.custom_category_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.core.presentation.components.DialogCard
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.util.UtilDrawing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomCategoryListItem(
    categoryModel: CategoryModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val openDetailDialog = remember { mutableStateOf(false) }
    if (openDetailDialog.value) {
        DialogCard(
            onDismissRequest = { openDetailDialog.value = false },
            title = stringResource(id = R.string.category_detail),
            content = {
                Column {
                    Text(text = stringResource(id = R.string.category_name_colon) + " " + categoryModel.name)
                    Text(text = stringResource(id = R.string.updated_on_colon) + " " + categoryModel.savedDate)
                }
            },
            positiveButton = {
                OutlinedButton(
                    onClick = { onEditClick() }
                ) {
                    Text(text = stringResource(id = R.string.edit))
                }
            },
            negativeButton = {
                OutlinedButton(
                    onClick = { openDetailDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
    
    val openDeleteDialog = remember { mutableStateOf(false) }
    if (openDeleteDialog.value) {
        DialogCard(
            onDismissRequest = { openDeleteDialog.value = false },
            title = stringResource(id = R.string.warning),
            content = {
                Text(text = stringResource(id = R.string.quest_do_you_want_to_delete_item))
            },
            positiveButton = {
                OutlinedButton(
                    onClick = {
                        openDeleteDialog.value = false
                        onDeleteClick()
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            negativeButton = {
                OutlinedButton(
                    onClick = { openDeleteDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    val dropdownMenuExpanded = rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 2.dp, 12.dp, 2.dp)
            .combinedClickable(
                onClick = {
                    openDetailDialog.value = true
                },
                onLongClick = {
                    dropdownMenuExpanded.value = true
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = categoryModel.name)
        categoryModel.image?.let {
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.new_category_drawable_size))
                    .clip(CircleShape),
                bitmap = UtilDrawing.bytesToBitmap(categoryModel.image)?.asImageBitmap() ?:
                UtilDrawing.createDefaultBitmap(1, 1).asImageBitmap(),
                contentDescription = "Image",
                contentScale = ContentScale.Crop
            )
        }
        /* dropdown menu will open when an item is long clicked */
        DropdownMenu(
            expanded = dropdownMenuExpanded.value,
            onDismissRequest = {
                dropdownMenuExpanded.value = false
            }
        ) {
            DropdownMenuItem(
                onClick = {
                    dropdownMenuExpanded.value = false
                    openDeleteDialog.value = true
                }
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
        }
    }
}