package com.kakeibo.ui.adapter.view

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.data.Item
import com.kakeibo.databinding.RowListReportCBinding
import com.kakeibo.ui.model.ReportCListRowModel
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.util.UtilCategory
import java.math.BigDecimal
import java.math.RoundingMode

class ReportCListAdapter(
        private val categoryColor: Int,
        private val categoryViewModel: CategoryViewModel,
        private val lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<ReportCListAdapter.ViewHolder>() {

    private var _itemList: List<ReportCListRowModel> = ArrayList()

    private var _itemMap: Map<Pair<Int, BigDecimal>, List<Item>> = HashMap()

    fun setAllByCategory(itemMap: Map<Pair<Int, BigDecimal>, List<Item>>) {
        _itemMap = itemMap

        /* filling the ReportCList */
        val reportCListRowList = itemMap.keys.map { ReportCListRowModel(it.first, it.second) }.toList().sortedBy { it.amount }
        val sumExpense = itemMap.keys.sumOf { it.second }
        val sizeIncome = Constants.CATEGORY_INCOME_COLORS.size
        val sizeExpense = Constants.CATEGORY_EXPENSE_COLORS.size
        for ((i, reportCRow) in reportCListRowList.withIndex()) {
            val percentage = reportCRow.amount
                    .multiply(BigDecimal(100))
                    .divide(sumExpense, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN)
            reportCRow.percentage =
                    if (percentage.toString().length==1) "0$percentage%"
                    else "$percentage%"
            when (categoryColor) {
                UtilCategory.CATEGORY_COLOR_INCOME ->
                    reportCRow.color =
                            if (i < sizeIncome) Constants.CATEGORY_INCOME_COLORS[i].toColorInt()
                            else Constants.CATEGORY_INCOME_COLORS[sizeIncome - 1].toColorInt()
                UtilCategory.CATEGORY_COLOR_EXPENSE ->
                    reportCRow.color =
                            if (i < sizeExpense) Constants.CATEGORY_EXPENSE_COLORS[i].toColorInt()
                            else Constants.CATEGORY_EXPENSE_COLORS[sizeExpense - 1].toColorInt()
            }
        }
        _itemList = reportCListRowList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowListReportCBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reportCListRowModel = _itemList[position]
        holder.bind(lifecycleOwner, reportCListRowModel, categoryViewModel)
        holder.binding.lnlRowListReportC.setOnClickListener { v ->
            val map = _itemMap.mapKeys { it.key.first }
            val listView = ListView(v.context)

            map[reportCListRowModel.categoryCode]?.let { list ->
                val adapter = ReportCDetailListAdapter(v.context, R.layout.row_list_report_c_detail, list, categoryViewModel)
                listView.adapter = adapter
                AlertDialog.Builder(v.context)
                        .setIcon(R.mipmap.ic_mikan)
                        .setTitle(categoryViewModel.allMap.value!![reportCListRowModel.categoryCode]!!.name)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .setView(listView).create()
                        .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return _itemList.size
    }

    class ViewHolder(val binding: RowListReportCBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(lifecycleOwner: LifecycleOwner, reportCListRowModel: ReportCListRowModel, categoryViewModel: CategoryViewModel) {
            binding.lifecycleOwner = lifecycleOwner
            binding.reportCListRowModel = reportCListRowModel
            binding.categoryViewModel = categoryViewModel
            binding.executePendingBindings()
        }
    }
}