package com.kakeibo.ui.adapter.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.*
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.Item
import com.kakeibo.databinding.DialogItemDetailBinding
import com.kakeibo.databinding.DialogItemEditBinding
import com.kakeibo.databinding.RowExplistChildBinding
import com.kakeibo.databinding.RowExplistParentBinding
import com.kakeibo.ui.MainActivity
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.ui.viewmodel.ItemViewModel
import com.kakeibo.util.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class ExpandableListAdapter(
        private val _itemViewModel: ItemViewModel,
        private val _categoryViewModel: CategoryViewModel,
        private val _lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _expandableList: MutableList<ExpandableListRowModel> = mutableListOf()
    private var _masterMap: SortedMap<ExpandableListRowModel.Header, List<Item>> = TreeMap()

    fun setData(masterMap: SortedMap<ExpandableListRowModel.Header, List<Item>>, date: String) {
        _masterMap = masterMap
        UtilExpandableList.expandOnlySpecificDate(_masterMap, _expandableList, date)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ExpandableListRowModel.PARENT -> {
                val rowExplistParentBinding = RowExplistParentBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false)
                ItemParentViewHolder(rowExplistParentBinding)
            }
            ExpandableListRowModel.CHILD -> {
                val rowExplistChildBinding = RowExplistChildBinding.inflate(
                                LayoutInflater.from(parent.context),
                                parent,
                                false)
                ItemChildViewHolder(rowExplistChildBinding)
            }
            else -> throw Exception("unknown item type")
        }
    }

    override fun getItemCount(): Int = _expandableList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = _expandableList[position]

        when(row.type){
            ExpandableListRowModel.PARENT -> {
                (holder as ItemParentViewHolder).bind(row)
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
                (holder as ItemChildViewHolder).bind(_lifecycleOwner, row.itemChild, _categoryViewModel)
                holder.childBinding.rllExpListChild.setOnClickListener {
                    val binding = DialogItemDetailBinding.inflate(LayoutInflater.from(it.context))
                    binding.item = row.itemChild
                    binding.categoryViewModel = _categoryViewModel

                    AlertDialog.Builder(it.context)
                            .setIcon(R.mipmap.ic_mikan)
                            .setTitle(it.context.getString(R.string.item_detail))
                            .setView(binding.root)
                            .setPositiveButton(R.string.close) { _, _ -> }
                            .setNegativeButton(R.string.delete) {_, _ -> showDeleteDialog(it, row.itemChild) }
                            .setNeutralButton(R.string.edit) { _, _ -> showEditDialog(it, row.itemChild) }
                            .show()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = _expandableList[position].type

    private fun expandRow(position: Int, header: ExpandableListRowModel.Header) {
        var nextPosition = position

        _masterMap[header]?.let {
            _expandableList.addAll(++nextPosition, it.map { child ->
                ExpandableListRowModel(ExpandableListRowModel.CHILD, child)
            })
        }
        notifyDataSetChanged()
    }

    private fun collapseRow(position: Int){
        val row = _expandableList[position]
        var nextPosition = position + 1

        when (row.type) {
            ExpandableListRowModel.PARENT -> {
                outerloop@ while (true) {
                    if (nextPosition == _expandableList.size
                            || _expandableList[nextPosition].type == ExpandableListRowModel.PARENT) {
                        break@outerloop
                    }
                    _expandableList.removeAt(nextPosition)
                }
                notifyDataSetChanged()
            }
        }
    }

    class ItemParentViewHolder(val parentBinding: RowExplistParentBinding)
        : RecyclerView.ViewHolder(parentBinding.root) {

        fun bind(header: ExpandableListRowModel) {
            parentBinding.header = header
        }
    }

    class ItemChildViewHolder(val childBinding: RowExplistChildBinding)
        : RecyclerView.ViewHolder(childBinding.root) {

        fun bind(lifecycleOwner: LifecycleOwner, item: Item, categoryViewModel: CategoryViewModel) {
            childBinding.lifecycleOwner = lifecycleOwner
            childBinding.item = item
            childBinding.categoryViewModel = categoryViewModel
        }
    }

    private fun showDeleteDialog(v: View, item: Item) {
        AlertDialog.Builder(v.context)
                .setIcon(R.mipmap.ic_mikan)
                .setTitle(v.context.getString(R.string.quest_do_you_want_to_delete_item))
                .setPositiveButton(R.string.yes) { _, _ ->
                    _itemViewModel.delete(item.id)
                    Toast.makeText(v.context,
                            v.context.getString(R.string.msg_item_successfully_deleted),
                            Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.no, null)
                .show()
    }

    private fun showEditDialog(v: View, item: Item) {
        val binding = DialogItemEditBinding.inflate(LayoutInflater.from(v.context))
        binding.lifecycleOwner = _lifecycleOwner
        binding.itemStatus = item

        /* event date */
        val eventDates = item.eventDate.split("-")
        val cal = GregorianCalendar(eventDates[0].toInt(), eventDates[1].toInt()-1, eventDates[2].toInt())
        val date = cal.time
        val dateText = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
                Locale.getDefault()).format(date)
                .toString() + " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
        binding.btnEventDate.text = dateText
        binding.btnEventDate.setOnClickListener {
            val ymd = UtilDate.getDBDate(dateText.split(" ")[0], MainActivity.dateFormat).split("-")
            val year = ymd[0].toInt()
            val month = ymd[1].toInt()
            val day = ymd[2].toInt()
            val dialog = DatePickerDialog(v.context, { _, y, m, d ->
                val gCal = GregorianCalendar(y, m, d)
                val str: String = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
                        Locale.getDefault()).format(gCal.time).toString() +
                        " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                binding.btnEventDate.text = str
            }, year, month - 1, day)
            dialog.show()
        }
        /* category */
        binding.btnCategory.text =
                if (item.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                    v.context.resources.getStringArray(R.array.default_category)[item.categoryCode]
                }
                else {
                    _categoryViewModel.allMap.value!![item.categoryCode]!!.name
                }
        binding.btnCategory.hint = "" + item.categoryCode
        binding.btnCategory.setOnClickListener {
            /* ordered by location */
            val adapter = CategoryListAdapter(v.context, 0, _categoryViewModel.all.value!!)
            val builder = androidx.appcompat.app.AlertDialog.Builder(v.context)
            val dInflater = v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val convertView: View = dInflater.inflate(R.layout.dialog_bas_search_category, null)
            builder.setView(convertView)
            builder.setCancelable(true)
            builder.setIcon(R.mipmap.ic_mikan)
            builder.setTitle(R.string.category)
            builder.setNegativeButton(R.string.cancel) { _, _ -> }
            val lv: ListView = convertView.findViewById(R.id.lsv_base_search_category)
            lv.adapter = adapter
            val dialog: Dialog = builder.show()
            lv.setOnItemClickListener { _, _, pos: Int, _ ->
                val selectedCategoryCode: Int = _categoryViewModel.all.value!![pos].code
                binding.btnCategory.text =
                        if (selectedCategoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                            v.context.resources.getStringArray(R.array.default_category)[selectedCategoryCode]
                        }
                        else {
                            _categoryViewModel.allMap.value!![selectedCategoryCode]!!.name
                        }
                binding.btnCategory.hint = "" + selectedCategoryCode
                dialog.dismiss()
            }
        }
        /* amount */
        binding.edtAmount.addTextChangedListener(AmountTextWatcher(binding.edtAmount))
        binding.edtAmount.setText(java.lang.String.valueOf(item.amount.abs()))
        /* memo */
        binding.edtMemo.setText(item.memo)

        AlertDialog.Builder(v.context)
                .setIcon(R.mipmap.ic_mikan)
                .setTitle(v.context.getString(R.string.edit_item))
                .setView(binding.root)
                .setPositiveButton(R.string.save) { _, _ ->
                    val categoryCode = binding.btnCategory.hint.toString().toInt()
                    val eventDate = UtilDate.getDBDate(binding.btnEventDate.text.toString().split(" ")[0], MainActivity.dateFormat)
                    val updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
                    val amount = when (_categoryViewModel.allMap.value!![categoryCode]!!.color) {
                        UtilCategory.CATEGORY_COLOR_INCOME -> {
                            BigDecimal(binding.edtAmount.text.toString())
                        }
                        UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                            BigDecimal(binding.edtAmount.text.toString()).negate()
                        }
                        else -> {
                            BigDecimal(0)
                        }
                    }

                    val itemStatus = Item(
                            item.id,
                            amount,
                            UtilCurrency.CURRENCY_NONE,
                            categoryCode,
                            binding.edtMemo.text.toString(),
                            eventDate,
                            updateDate
                    )

                    val result = UtilText.checkBeforeSave(binding.edtAmount.text.toString())
                    if (!result.first) {
                        Toast.makeText(v.context,
                                v.context.getString(result.second),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        _itemViewModel.insert(itemStatus)

                        Toast.makeText(v.context,
                                v.context.getString(R.string.msg_change_successfully_saved),
                                Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }
}