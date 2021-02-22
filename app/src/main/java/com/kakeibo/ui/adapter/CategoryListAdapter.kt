package com.kakeibo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.databinding.RowListCategoryBinding

class CategoryListAdapter(context: Context, resource: Int, categoryList: List<CategoryStatus>)
    : ArrayAdapter<CategoryStatus>(context, resource, categoryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView

        val binding: RowListCategoryBinding
        if (rowView == null) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_list_category, parent, false)
            rowView = binding.root
        } else {
            binding = rowView.tag as RowListCategoryBinding
        }

        binding.categoryStatus = getItem(position)
        rowView.tag = binding
        return rowView
    }
}