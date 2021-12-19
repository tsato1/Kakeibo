package com.kakeibo.feature_main.presentation.item_input

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kakeibo.R
import com.kakeibo.feature_main.domain.models.DisplayedCategory
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing

@Composable
fun GridCategoryItem(
    modifier: Modifier = Modifier,
    displayedCategory: DisplayedCategory,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onItemClick() },
                    onLongPress ={ onItemLongClick() }
                )
            },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                displayedCategory.code < UtilCategory.CUSTOM_CATEGORY_CODE_START -> {
                    Image(
                        painter = painterResource(
                            id = UtilDrawing.getDrawableIdFromIconName(
                                LocalContext.current,
                                displayedCategory.drawable
                            )
                        ),
                        contentDescription = "Category Icon"
                    )
                }
                displayedCategory.image != null -> {
                    Image(
                        bitmap = //todo: default value
                        UtilDrawing.bytesToBitmap(displayedCategory.image)?.asImageBitmap() ?:
                        Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888).asImageBitmap(),
                        contentDescription = "Category Icon"
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.mipmap.ic_launcher),
                        contentDescription = "Not Found"
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayedCategory.name,
                style = MaterialTheme.typography.body1
            )
        }
    }
}