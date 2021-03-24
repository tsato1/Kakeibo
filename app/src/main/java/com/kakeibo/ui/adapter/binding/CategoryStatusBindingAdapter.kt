package com.kakeibo.ui.adapter.binding

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing.bytesToBitmap
import com.kakeibo.util.UtilDrawing.getDrawableIdFromIconName

@BindingAdapter("bind:context", "bind:category")
fun setImage(imageView: ImageView, context: Context, categoryStatus: CategoryStatus) {
    if (categoryStatus.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
        imageView.setImageResource(
                getDrawableIdFromIconName(context, categoryStatus.drawable))
    } else {
        categoryStatus.image?.let {
            imageView.setImageBitmap(bytesToBitmap(it))
        }
    }
}

@BindingAdapter("bind:context", "bind:categoryCode", "bind:categoryViewModel")
fun setImage(imageView: ImageView, context: Context, categoryCode: Int, categoryStatusViewModel: CategoryStatusViewModel?) {
    categoryStatusViewModel?.let {
        val categoryStatus = it.allMap.value!![categoryCode]

        categoryStatus?.let {
            setImage(imageView, context, categoryStatus)
        }
    }
}

@BindingAdapter("bind:context", "bind:categoryCode", "bind:colon", "bind:categoryViewModel")
fun setName(textView: TextView, context: Context, categoryCode: Int, colon: Boolean, categoryStatusViewModel: CategoryStatusViewModel?) {
    categoryStatusViewModel?.let {
        val category = it.allMap.value!![categoryCode]
        setName(textView, context, category, colon)
    }
}

@BindingAdapter("bind:context", "bind:category", "bind:colon")
fun setName(textView: TextView, context: Context, category: CategoryStatus?, colon: Boolean) {
    category?.let {
        val text = if (colon) context.getString(R.string.category_colon) + " " + it.name else it.name
        textView.text = text
    }
}