//package com.kakeibo.ui.adapter.view
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import com.kakeibo.feature_item.data.sources.local.entities.Item
//import com.kakeibo.databinding.RowListReportCDetailBinding
//import com.kakeibo.feature_settings.settings_category.presentation.CategoryViewModel
//
//class ReportCDetailListAdapter(
//        context: Context,
//        resource: Int,
//        itemList: List<Item>,
//        private val categoryViewModel: CategoryViewModel)
//    : ArrayAdapter<Item>(context, resource, itemList) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var rowView = convertView
//
//        val binding: RowListReportCDetailBinding
//        if (rowView == null) {
//            binding = RowListReportCDetailBinding.inflate(LayoutInflater.from(parent.context))
//            rowView = binding.root
//        } else {
//            binding = rowView.tag as RowListReportCDetailBinding
//        }
//
//        binding.itemStatus = getItem(position)
//        binding.categoryViewModel = categoryViewModel
//        rowView.tag = binding
//        return rowView
//    }
//}