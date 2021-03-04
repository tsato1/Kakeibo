package com.kakeibo.ui.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.ui.MainActivity
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

@BindingAdapter("bind:context", "bind:categoryCode")
fun setImage(imageView: ImageView, context: Context, categoryCode: Int) {
    val categoryStatus = MainActivity.allCategoryMap[categoryCode]

    categoryStatus?.let {
        setImage(imageView, context, categoryStatus)
    }
}

@BindingAdapter("bind:context", "bind:categoryCode", "bind:colon")
fun setName(textView: TextView, context: Context, categoryCode: Int, colon: Boolean) {
    val categoryStatus = MainActivity.allCategoryMap[categoryCode]
    val text =
            if(colon) context.getString(R.string.category_colon) + " " + categoryStatus?.name
            else categoryStatus?.name
    textView.text = text
}