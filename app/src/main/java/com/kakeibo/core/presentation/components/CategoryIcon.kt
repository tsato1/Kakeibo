package com.kakeibo.core.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.kakeibo.R
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    code: Int,
    drawable: String,
    image: ByteArray?,
    size: Dp = dimensionResource(id = R.dimen.new_category_drawable_size)
) {
    when {
        code < 0 -> {
            Icon(
                modifier = modifier,
                painter = painterResource(id = R.mipmap.ic_mikan),
                contentDescription = "Icon Not Found",
                tint= Color.Unspecified
            )
        }
        code < UtilCategory.CUSTOM_CATEGORY_CODE_START -> {
            /* Invalid Resource ID */
            if (UtilDrawing.getDrawableIdFromIconName(LocalContext.current, drawable) == 0) {
                Icon(
                    modifier = modifier,
                    painter = painterResource(id = R.mipmap.ic_mikan),
                    contentDescription = "Icon Not Found",
                    tint= Color.Unspecified
                )
                return
            }

            Image(
                modifier = modifier.size(size),
                painter = painterResource(
                    id = UtilDrawing.getDrawableIdFromIconName(
                        LocalContext.current,
                        drawable
                    )
                ),
                contentDescription = "Category Icon"
            )
        }
        UtilCategory.CUSTOM_CATEGORY_CODE_START <= code -> {
            Image(
                modifier = modifier.size(size),
                bitmap = UtilDrawing.bytesToBitmap(image)
                    ?.let {
                        UtilDrawing.getBitmapClippedCircle(it).asImageBitmap()
                    } ?: Bitmap.createBitmap(
                    0,
                    0,
                    Bitmap.Config.ARGB_8888
                ).asImageBitmap(),
                contentDescription = "Category Icon"
            )
        }
    }

}