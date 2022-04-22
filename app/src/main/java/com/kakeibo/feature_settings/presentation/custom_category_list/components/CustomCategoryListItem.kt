package com.kakeibo.feature_settings.presentation.custom_category_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.util.UtilDrawing

@Composable
fun CustomCategoryListItem(
    categoryModel: CategoryModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val openDetailDialog = remember { mutableStateOf(false) }
    if (openDetailDialog.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            title = {
                Column {
                    Text(text = stringResource(id = R.string.category_colon) + " " + categoryModel.name)
                    Text(text = stringResource(id = R.string.updated_on_colon) + " " + categoryModel.savedDate)
                }
            },
            onDismissRequest = { openDetailDialog.value = false },
            dismissButton = {
                OutlinedButton(onClick = { openDetailDialog.value = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { onEditClick() }) {
                    Text(text = stringResource(id = R.string.edit))
                }
            },
            shape = RoundedCornerShape(15.dp)
        )
    }
    
    val openDeleteDialog = remember { mutableStateOf(false) }
    if (openDeleteDialog.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background),
            title = {
                Text(text = stringResource(id = R.string.quest_do_you_want_to_delete_item))
            },
            onDismissRequest = { openDeleteDialog.value = false },
            dismissButton = {
                OutlinedButton(onClick = { openDetailDialog.value = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colors.onBackground
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        openDeleteDialog.value = false
                        onDeleteClick()
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            shape = RoundedCornerShape(15.dp)
        )
    }

    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { openDetailDialog.value = true },
                    onLongPress = { openDeleteDialog.value = true }
                )
            }
            .height(80.dp),
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
    }
}