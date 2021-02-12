package com.kakeibo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.util.UtilCategory

class CategoryListAdapter(
        context: Context,
        resource: Int,
        objects: List<CategoryStatus?>?
) : ArrayAdapter<CategoryStatus?>(context, resource, objects!!) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val categoryStatus = getItem(position)
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.row_category_selection, null)
            val txvCategory = convertView.findViewById<TextView>(R.id.txv_category)
            val imvCategory = convertView.findViewById<ImageView>(R.id.imv_category)
            val viewHolder: ViewHolder = ViewHolder(imvCategory, txvCategory)
            convertView.tag = viewHolder
        }
        val viewHolder = convertView!!.tag as ViewHolder

//        String categoryText = UtilCategory.getCategoryStr(getContext(), categoryStatus.getCode());
//        viewHolder.txvCategory.setText(categoryText);

        //should be disposable
//        if (categoryStatus.getDrawable() == -1) { // ==-1: category is created by user -> use byte array
//            viewHolder.imvCategory.setImageBitmap(
//                    UtilDrawing.bytesToBitmap(categoryStatus.getImage()));
//        } else { // default category -> use drawable
//            viewHolder.imvCategory.setImageDrawable(
//                    ContextCompat.getDrawable(_context, categoryStatus.getDrawable()));
//        }
        if (categoryStatus!!.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) { // default category -> use drawable
//            viewHolder.build(categoryStatus.getName(),
//                    UtilDrawing.getDrawableIdFromIconName(getContext(), categoryStatus.getDrawable()));
        } else { // category is created by user -> use byte array
//            viewHolder.build(categoryStatus.getName(), categoryStatus.getImage());
        }
        return convertView
    }

    private inner class ViewHolder internal constructor(private val imvCategory: ImageView, private val txvCategory: TextView)

}