package com.kakeibo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.data.CategoryStatus
import com.kakeibo.databinding.ItemGridBinding
import com.kakeibo.ui.listener.CategoryClickListener
import java.util.*

class CategoryGridAdapter(private val _categoryClickListener : CategoryClickListener)
    : RecyclerView.Adapter<CategoryGridAdapter.ViewHolder>() {

    private var _categoryStatusList: List<CategoryStatus>? = ArrayList()

    fun setCategoryStatuses(categoryStatusList: List<CategoryStatus>?) {
        _categoryStatusList = categoryStatusList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryStatus = _categoryStatusList!![position]
        holder.bind(categoryStatus, _categoryClickListener)
    }

    override fun getItemCount(): Int {
        return if (_categoryStatusList != null) _categoryStatusList!!.size else 0
    }

    class ViewHolder(private val binding: ItemGridBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryStatus: CategoryStatus, categoryClickListener: CategoryClickListener) {
            binding.category = categoryStatus
            binding.categoryClickListener = categoryClickListener
            binding.executePendingBindings()
        }
    }
}