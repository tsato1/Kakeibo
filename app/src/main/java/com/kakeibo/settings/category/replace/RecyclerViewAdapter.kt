package com.kakeibo.settings.category.replace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.databinding.ItemGridBinding
import com.kakeibo.ui.listener.CategoryClickListener

class RecyclerViewAdapter(
        private val list: List<GridItem>,
        private val _categoryClickListener : CategoryClickListener?)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = list[position].itemType

    override fun getItemId(position: Int): Long = list[position].id

    fun getList(): List<GridItem> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (viewType) {
            GridItem.ITEM_TYPE_HEADER -> {
                ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
            }
            GridItem.ITEM_TYPE_PARENT -> {
                ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
            }
            GridItem.ITEM_TYPE_CHILD -> {
                ItemGridBinding.inflate(LayoutInflater.from(parent.context), parent ,false)
            }
            else -> throw Exception("unknown item type")
        }
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryStatus = list[position]

        when (getItemViewType(position)) {
            GridItem.ITEM_TYPE_CHILD -> {
                holder.bind(categoryStatus, _categoryClickListener)
            }
            GridItem.ITEM_TYPE_PARENT -> {
            }
            else -> throw Exception("unknown item type")
        }
    }

    class ViewHolder(val binding: ItemGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GridItem, categoryClickListener: CategoryClickListener?) {
            binding.category = item.category
            binding.categoryClickListener = categoryClickListener
            binding.executePendingBindings()
        }
    }
}