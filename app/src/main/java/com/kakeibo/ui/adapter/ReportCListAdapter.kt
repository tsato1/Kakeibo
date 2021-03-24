package com.kakeibo.ui.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.RowListReportCBinding
import com.kakeibo.ui.MainActivity
import com.kakeibo.ui.model.ReportCListRowModel
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.util.UtilCategory
import java.math.BigDecimal
import java.math.RoundingMode

class ReportCListAdapter(
        private val categoryColor: Int,
        private val categoryStatusViewModel: CategoryStatusViewModel)
    : RecyclerView.Adapter<ReportCListAdapter.ViewHolder>() {

    private var _itemList: List<ReportCListRowModel>? = ArrayList()

    private var _itemMap: Map<Pair<Int, BigDecimal>, List<ItemStatus>>? = HashMap()

    fun setAllByCategory(itemMap: Map<Pair<Int, BigDecimal>, List<ItemStatus>>?) {
        _itemMap = itemMap

        /* filling the ReportCList */
        val reportCListRowList = itemMap!!.keys.map { ReportCListRowModel(it.first, it.second) }.toList()
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
        val binding: RowListReportCBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.row_list_report_c, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reportCListRowModel = _itemList!![position]
        holder.bind(reportCListRowModel)
        holder.binding.categoryViewModel = categoryStatusViewModel
        holder.binding.lnlRowListReportC.setOnClickListener { v ->
            _itemMap?.let {
                val map = it.mapKeys { map -> map.key.first }
                val listView = ListView(v.context)

                map[reportCListRowModel.categoryCode]?.let { list ->
                    val adapter = ReportCDetailListAdapter(v.context, R.layout.row_list_report_c_detail, list, categoryStatusViewModel)
                    listView.adapter = adapter
                    AlertDialog.Builder(v.context)
                            .setIcon(R.mipmap.ic_mikan)
                            .setTitle(MainActivity.allCategoryMap[reportCListRowModel.categoryCode]!!.name)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .setView(listView).create()
                            .show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (_itemList != null) _itemList!!.size else 0
    }

    class ViewHolder(val binding: RowListReportCBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(reportCListRowModel: ReportCListRowModel) {
            binding.reportCListRowModel = reportCListRowModel
            binding.executePendingBindings()
        }
    }
}