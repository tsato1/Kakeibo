package com.kakeibo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.RowExplistChildBinding
import com.kakeibo.databinding.RowExplistParentBinding
import com.kakeibo.ui.listener.ItemClickListener
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.util.UtilExpandableList
import java.util.*

class ExpandableListAdapter(
        private var expandableList: MutableList<ExpandableListRowModel>,
        private var _itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _masterMap: SortedMap<String, List<ItemStatus>> = TreeMap()

    fun setMasterMap(masterMap: SortedMap<String, List<ItemStatus>>) {
        _masterMap = masterMap
        UtilExpandableList.expandOnlySpecificDate(_masterMap, expandableList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ExpandableListRowModel.PARENT -> {
                val rowExplistParentBinding: RowExplistParentBinding = DataBindingUtil
                        .inflate(
                                LayoutInflater.from(parent.context),
                                R.layout.row_explist_parent,
                                parent,
                                false)
                ItemParentViewHolder(rowExplistParentBinding)
            }
            ExpandableListRowModel.CHILD -> {
                val rowExplistChildBinding: RowExplistChildBinding = DataBindingUtil
                        .inflate(
                                LayoutInflater.from(parent.context),
                                R.layout.row_explist_child,
                                parent,
                                false)
                ItemChildViewHolder(rowExplistChildBinding)
            }
            else -> {
                val rowExplistParentBinding: RowExplistParentBinding = DataBindingUtil
                        .inflate(
                                LayoutInflater.from(parent.context),
                                R.layout.row_explist_parent,
                                parent,
                                false)
                ItemParentViewHolder(rowExplistParentBinding)
            }
        }
    }

    override fun getItemCount(): Int = expandableList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = expandableList[position]

        when(row.type){
            ExpandableListRowModel.PARENT -> {
                (holder as ItemParentViewHolder).bind(row.itemParent)
                holder.parentBinding.lnlExpListParent.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        holder.parentBinding.upArrow.visibility = View.GONE
                        holder.parentBinding.closeArrow.visibility = View.VISIBLE
                        collapseRow(position)
                    } else {
                        row.isExpanded = true
                        holder.parentBinding.upArrow.visibility = View.VISIBLE
                        holder.parentBinding.closeArrow.visibility = View.GONE
                        expandRow(position, row.itemParent)
                    }
                }
            }
            ExpandableListRowModel.CHILD -> {
                (holder as ItemChildViewHolder).bind(row.itemChild, _itemClickListener)
            }
        }

    }

    override fun getItemViewType(position: Int): Int = expandableList[position].type

    private fun expandRow(position: Int, date: String) {
        var nextPosition = position

        _masterMap[date]?.let {
            expandableList.addAll(++nextPosition, it.map { child ->
                ExpandableListRowModel(ExpandableListRowModel.CHILD, child)
            })
        }
        notifyDataSetChanged()
    }

    private fun collapseRow(position: Int){
        val row = expandableList[position]
        var nextPosition = position + 1

        when (row.type) {
            ExpandableListRowModel.PARENT -> {
                outerloop@ while (true) {
                    if (nextPosition == expandableList.size || expandableList[nextPosition].type == ExpandableListRowModel.PARENT) {
                        break@outerloop
                    }
                    expandableList.removeAt(nextPosition)
                }
                notifyDataSetChanged()
            }
        }
    }

    class ItemParentViewHolder(val parentBinding: RowExplistParentBinding)
        : RecyclerView.ViewHolder(parentBinding.root) {

        fun bind(header: String) {
            parentBinding.txvHeaderDate.text = header

        }
    }

    class ItemChildViewHolder(private val childBinding: RowExplistChildBinding)
        : RecyclerView.ViewHolder(childBinding.root) {

        fun bind(itemStatus: ItemStatus, itemClickListener: ItemClickListener) {
            childBinding.itemStatus = itemStatus
            childBinding.itemClickListener = itemClickListener
        }
    }
}