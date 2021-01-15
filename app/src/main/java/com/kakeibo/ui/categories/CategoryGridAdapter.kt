package com.kakeibo.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.CategoryStatus
import com.kakeibo.databinding.ItemGridBinding
import com.kakeibo.ui.ItemSaveListener
import java.util.*

/*
* Used in TabFragment1, Settings
*/
class CategoryGridAdapter(
        private val _itemSaveListener: ItemSaveListener
        ) : RecyclerView.Adapter<CategoryGridAdapter.ViewHolder>() {

    private var _categoryStatusList: List<CategoryStatus>? = ArrayList()

    fun setCategoryStatuses(categoryStatusList: List<CategoryStatus>?) {
        _categoryStatusList = categoryStatusList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemGridBinding: ItemGridBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_grid, parent, false)
        return ViewHolder(itemGridBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryStatus = _categoryStatusList!![position]
        holder.bind(categoryStatus, _itemSaveListener)
    }

    override fun getItemCount(): Int {
        return if (_categoryStatusList != null) _categoryStatusList!!.size else 0
    }

    class ViewHolder(
            private val itemGridBinding: ItemGridBinding
            ) : RecyclerView.ViewHolder(itemGridBinding.root) {

        fun bind(categoryStatus: CategoryStatus?, itemSaveListener: ItemSaveListener?) {
            itemGridBinding.category = categoryStatus
            itemGridBinding.itemSaveListener = itemSaveListener
            itemGridBinding.executePendingBindings()
        }
    }
}