package com.kakeibo.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.RowListReportCDetailBinding
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel

class ReportCDetailListAdapter(
        context: Context,
        resource: Int,
        itemList: List<ItemStatus>,
        private val categoryStatusViewModel: CategoryStatusViewModel)
    : ArrayAdapter<ItemStatus>(context, resource, itemList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView

        val binding: RowListReportCDetailBinding
        if (rowView == null) {
            binding = RowListReportCDetailBinding.inflate(LayoutInflater.from(parent.context))
            rowView = binding.root
        } else {
            binding = rowView.tag as RowListReportCDetailBinding
        }

        binding.itemStatus = getItem(position)
        binding.categoryViewModel = categoryStatusViewModel
        rowView.tag = binding
        return rowView
    }
}