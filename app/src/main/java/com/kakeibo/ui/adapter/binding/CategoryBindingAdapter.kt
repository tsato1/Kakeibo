//package com.kakeibo.ui.adapter.binding
//
//import android.content.Context
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.databinding.BindingAdapter
//import com.kakeibo.R
//import com.kakeibo.core.data.local.entities.CategoryEntity
//import com.kakeibo.util.UtilCategory
//import com.kakeibo.util.UtilDrawing.bytesToBitmap
//import com.kakeibo.util.UtilDrawing.getDrawableIdFromIconName
//
//@BindingAdapter("bind:context", "bind:category")
//fun setImage(imageView: ImageView, context: Context, category: CategoryEntity?) {
//    category?.let {
//        if (category.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
//            imageView.setImageResource(getDrawableIdFromIconName(context, category.drawable))
//        } else {
//            category.image?.let {
//                imageView.setImageBitmap(bytesToBitmap(it))
//            }
//        }
//    }
//}
//
//@BindingAdapter("bind:context", "bind:categoryCode", "bind:masterMap")
//fun setImage(imageView: ImageView, context: Context, categoryCode: Int, masterMap: Map<Int, CategoryEntity>?) {
//    masterMap?.let {
//        val category = it[categoryCode]
//        setImage(imageView, context, category)
//    }
//}
//
//@BindingAdapter("bind:context", "bind:category", "bind:colon")
//fun setName(textView: TextView, context: Context, category: CategoryEntity?, colon: Boolean) {
//    category?.let {
//        if (category.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
//            val name = context.resources.getStringArray(R.array.default_category)[category.code]
//            val text = if (colon) context.getString(R.string.category_colon) + " " + name else name
//            textView.text = text
//        }
//        else {
//            val text = if (colon) context.getString(R.string.category_colon) + " " + it.name else it.name
//            textView.text = text
//        }
//    }
//}
//
//@BindingAdapter("bind:context", "bind:categoryCode", "bind:colon", "bind:masterMap")
//fun setName(textView: TextView, context: Context, categoryCode: Int, colon: Boolean, masterMap: Map<Int, CategoryEntity>?) {
//    masterMap?.let {
//        val category = it[categoryCode]
//        setName(textView, context, category, colon)
//    }
//}