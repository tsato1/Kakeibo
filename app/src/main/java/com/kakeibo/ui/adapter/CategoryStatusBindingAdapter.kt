package com.kakeibo.ui.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.ItemStatus
import com.kakeibo.ui.MainActivity
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

@BindingAdapter("context", "categoryCode")
fun setImage(imageView: ImageView, context: Context?, categoryCode: Int) {
    val categoryStatus = (context as MainActivity).allCategoryStatusMap[categoryCode]

    categoryStatus?.let {
        setImage(imageView, context, categoryStatus)
    }
}

@BindingAdapter("context", "categoryCode")
fun setName(textView: TextView, context: Context?, categoryCode: Int) {
    val categoryStatus = (context as MainActivity).allCategoryStatusMap[categoryCode]
    textView.text = categoryStatus?.name
}

@BindingAdapter("context", "item")
fun setAmount(textView: TextView, context: Context?, itemStatus: ItemStatus) {
    val categoryStatus = (context as MainActivity).allCategoryStatusMap[itemStatus.categoryCode]

    var amount = SpannableString("")

    when (categoryStatus?.color) {
        UtilCategory.CATEGORY_COLOR_INCOME -> {
            amount = SpannableString("+" + itemStatus.getAmount())
            amount.setSpan(ForegroundColorSpan(Color.BLUE), 0, 1, 0)
        }
        UtilCategory.CATEGORY_COLOR_EXPENSE -> {
            amount = SpannableString("-" + itemStatus.getAmount())
            amount.setSpan(ForegroundColorSpan(Color.RED), 0, 1, 0)
        }
        else -> {

        }
    }

    textView.text = amount
}