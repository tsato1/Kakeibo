package com.kakeibo.core.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.kakeibo.R
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing

@Composable
fun CategoryIcon(
    modifier: Modifier = Modifier,
    code: Int,
    drawable: String,
    image: ByteArray?
) {
    when {
        code < UtilCategory.CUSTOM_CATEGORY_CODE_START -> {
            Image(
                modifier = modifier,
                painter = painterResource(
                    id = UtilDrawing.getDrawableIdFromIconName(
                        LocalContext.current,
                        drawable
                    )
                ),
                contentDescription = "Category Icon"
            )
        }
        image != null -> {
            val size =
                dimensionResource(id = R.dimen.new_category_drawable_size)
            Image(
                modifier = modifier.size(size),
                bitmap = UtilDrawing.bytesToBitmap(image)
                    ?.let {
                        UtilDrawing.getBitmapClippedCircle(it).asImageBitmap()
                    } ?: Bitmap.createBitmap(
                    10,
                    10,
                    Bitmap.Config.ARGB_8888
                ).asImageBitmap(),
                contentDescription = "Category Icon"
            )
        }
        else -> {
            Icon(
                modifier = modifier,
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Icon Not Found"
            )
        }
    }

}