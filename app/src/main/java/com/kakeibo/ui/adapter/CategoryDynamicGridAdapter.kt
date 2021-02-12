package com.kakeibo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.util.UtilDrawing.bytesToBitmap
import com.takahidesato.android.dynamicgrid.BaseDynamicGridAdapter

class CategoryDynamicGridAdapter(
        context: Context?,
        items: List<*>?,
        columnCount: Int
) : BaseDynamicGridAdapter(context, items, columnCount) {

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid, null)
//            holder = ViewHolder(convertView)
//            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        val categoryStatus = getItem(position) as CategoryStatus

//        if (categoryStatus.getCode() < UtilCategory.CUSTOM_CATEGORY_CODE_START) { // default category -> use drawable
//            holder.build(categoryStatus.getName(),
//                    UtilDrawing.getDrawableIdFromIconName(getContext(), categoryStatus.getDrawable()));
//        } else { // category is created by user -> use byte array
//            holder.build(categoryStatus.getName(), categoryStatus.getImage());
//        }
        return convertView
    }

    inner class ViewHolder private constructor(view: View) {
        private val titleText: TextView = view.findViewById(R.id.item_title)
        private val image: ImageView = view.findViewById(R.id.item_img)

        fun build(title: String?, drawable: Int) {
            titleText.text = title
            image.setImageResource(drawable)
        }

        fun build(title: String?, img: ByteArray?) {
            titleText.text = title
            image.setImageBitmap(bytesToBitmap(img!!))
        }
    }
}