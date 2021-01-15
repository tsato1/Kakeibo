package com.kakeibo.ui.categories

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kakeibo.data.CategoryStatus
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing.bytesToBitmap
import com.kakeibo.util.UtilDrawing.getDrawableIdFromIconName

@BindingAdapter("context", "category")
fun setImage(imageView: ImageView, context: Context?, categoryStatus: CategoryStatus) {

    if (categoryStatus.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
        imageView.setImageResource(
                getDrawableIdFromIconName(context!!, categoryStatus.drawable))
    } else {
        categoryStatus.image?.let {
            imageView.setImageBitmap(bytesToBitmap(it))
        }
    }
}
