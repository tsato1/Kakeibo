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

class ReportCDetailListAdapter(context: Context, resource: Int, itemList: List<ItemStatus>)
    : ArrayAdapter<ItemStatus>(context, resource, itemList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView

        val binding: RowListReportCDetailBinding
        if (rowView == null) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.row_list_report_c_detail, parent, false)
            rowView = binding.root
        } else {
            binding = rowView.tag as RowListReportCDetailBinding
        }

        binding.itemStatus = getItem(position)
        rowView.tag = binding
        return rowView
    }
}