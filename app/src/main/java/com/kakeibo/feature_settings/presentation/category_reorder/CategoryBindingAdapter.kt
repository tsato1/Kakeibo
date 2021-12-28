package com.kakeibo.feature_settings.presentation.category_reorder

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.kakeibo.R
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing.bytesToBitmap
import com.kakeibo.util.UtilDrawing.getBitmapClippedCircle
import com.kakeibo.util.UtilDrawing.getDrawableIdFromIconName

@BindingAdapter("bind:context", "bind:category")
fun setImage(imageView: ImageView, context: Context, category: CategoryModel?) {
    category?.let {
        if (category.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            imageView.setImageResource(getDrawableIdFromIconName(context, category.drawable))
        } else {
            val size = context.resources.getDimension(R.dimen.new_category_drawable_size).toInt()

            category.image?.let { byteArray ->
                bytesToBitmap(byteArray)?.let { bitmap ->
                    imageView.setImageBitmap(getBitmapClippedCircle(bitmap))
                    imageView.updateLayoutParams {
                        width = size
                        height = size
                    }
                }
            }
        }
    }
}

@BindingAdapter("bind:context", "bind:category", "bind:colon")
fun setName(textView: TextView, context: Context, category: CategoryModel?, colon: Boolean) {
    category?.let {
        if (category.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            val name = context.resources.getStringArray(R.array.default_category)[category.code]
            val text = if (colon) context.getString(R.string.category_colon) + " " + name else name
            textView.text = text
        }
        else {
            val text = if (colon) context.getString(R.string.category_colon) + " " + it.name else it.name
            textView.text = text
        }
    }
}