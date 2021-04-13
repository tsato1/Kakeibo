package com.kakeibo.ui.adapter.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.kakeibo.data.Category
import com.kakeibo.databinding.RowListCategoryBinding

class CategoryListAdapter(
        context: Context,
        resource: Int,
        categoryList: List<Category>)
    : ArrayAdapter<Category>(context, resource, categoryList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView

        val binding: RowListCategoryBinding
        if (rowView == null) {
            binding = RowListCategoryBinding.inflate(LayoutInflater.from(parent.context))
            rowView = binding.root
        } else {
            binding = rowView.tag as RowListCategoryBinding
        }

        binding.category = getItem(position)
        binding.executePendingBindings()
        rowView.tag = binding
        return rowView
    }
}