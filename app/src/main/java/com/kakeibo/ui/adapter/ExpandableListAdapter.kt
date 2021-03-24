package com.kakeibo.ui.adapter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.DialogItemDetailBinding
import com.kakeibo.databinding.RowExplistChildBinding
import com.kakeibo.databinding.RowExplistParentBinding
import com.kakeibo.ui.MainActivity
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.util.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


class ExpandableListAdapter(private val categoryViewModel: CategoryStatusViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var _itemStatusViewModel: ItemStatusViewModel
    fun setItemStatusViewMode(itemStatusViewModel: ItemStatusViewModel) {
        _itemStatusViewModel = itemStatusViewModel
    }

    private var _expandableList: MutableList<ExpandableListRowModel> = mutableListOf()
    fun setExpandableList(expandableList: MutableList<ExpandableListRowModel>) {
        _expandableList = expandableList
    }
    fun getExpandableList(): MutableList<ExpandableListRowModel> {
        return _expandableList
    }

    private var _masterMap: SortedMap<Pair<String, BigDecimal>, List<ItemStatus>> = TreeMap()
    fun setMasterMap(masterMap: SortedMap<Pair<String, BigDecimal>, List<ItemStatus>>) {
        _masterMap = masterMap
        UtilExpandableList.expandOnlySpecificDate(_masterMap, _expandableList)
        notifyDataSetChanged()
    }
    fun getMasterMap(): SortedMap<Pair<String, BigDecimal>, List<ItemStatus>> {
        return _masterMap
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
                (holder as ItemChildViewHolder).bind(row.itemChild)
                holder.childBinding.categoryViewModel = categoryViewModel
                holder.childBinding.rllExpListChild.setOnClickListener {
                    val binding = DialogItemDetailBinding.inflate(LayoutInflater.from(it.context))
                    binding.itemStatus = row.itemChild

                    AlertDialog.Builder(it.context)
                            .setIcon(R.mipmap.ic_mikan)
                            .setTitle(it.context.getString(R.string.item_detail))
                            .setView(binding.root)
                            .setPositiveButton(R.string.close) { _, _ -> }
                            .setNeutralButton(R.string.edit) { _, _ ->
                                showEditDialog(it, row.itemChild)
                            }
                            .show()
                }

                holder.childBinding.rllExpListChild.setOnLongClickListener {
                    AlertDialog.Builder(it.context)
                            .setIcon(R.mipmap.ic_mikan)
                            .setTitle(it.context.getString(R.string.quest_do_you_want_to_delete_item))
                            .setPositiveButton(R.string.yes) { _, _ ->
                                _itemStatusViewModel.delete(row.itemChild.id)
                                Toast.makeText(it.context,
                                        it.context.getString(R.string.msg_item_successfully_deleted),
                                        Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                    true
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int = _expandableList[position].type

    private fun expandRow(position: Int, header: Pair<String, BigDecimal>) {
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

        fun bind(itemStatus: ItemStatus) {
            childBinding.itemStatus = itemStatus
        }
    }

    private fun showEditDialog(v: View, item: ItemStatus) {
//        val binding = DialogItemEditBinding.inflate(LayoutInflater.from(v.context))
//        binding.itemStatus = item
        val inflater = v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.dialog_item_edit, v.findViewById(R.id.layout_root))

        /*** event date ***/
        val btnEventDate: Button = layout.findViewById(R.id.btn_event_date)
        val eventDates = item.eventDate.split("-")
        val cal = GregorianCalendar(eventDates[0].toInt(), eventDates[1].toInt()-1, eventDates[2].toInt())
        val date = cal.time
        val dateText = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
                Locale.getDefault()).format(date)
                .toString() + " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
        btnEventDate.text = dateText
        btnEventDate.setOnClickListener {
            val ymd = UtilDate.getDBDate(dateText.split(" ")[0], MainActivity.dateFormat).split("-")
            val year = ymd[0].toInt()
            val month = ymd[1].toInt()
            val day = ymd[2].toInt()
            val dialog = DatePickerDialog(v.context, { _, y, m, d ->
                val gCal = GregorianCalendar(y, m, d)
                val str: String = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
                        Locale.getDefault()).format(gCal.time).toString() +
                        " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                btnEventDate.text = str
            }, year, month - 1, day)
            dialog.show()
        }
        /*** category ***/
        val btnCategory: Button = layout.findViewById(R.id.btn_category)
        val categoryName: String = MainActivity.allCategoryMap[item.categoryCode]!!.name
        btnCategory.text = categoryName
        btnCategory.hint = "" + item.categoryCode
        btnCategory.setOnClickListener {
            /*** ordered by location  */
            val adapter = CategoryListAdapter(v.context, 0, MainActivity.allDspCategoryList) // todo exchange with CategoryViewModel
            val builder = androidx.appcompat.app.AlertDialog.Builder(v.context)
            val dInflater = v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val convertView: View = dInflater.inflate(R.layout.dialog_bas_search_category, null)
            builder.setView(convertView)
            builder.setCancelable(true)
            builder.setIcon(R.mipmap.ic_mikan)
            builder.setTitle(R.string.category)
            val lv: ListView = convertView.findViewById(R.id.lsv_base_search_category)
            lv.adapter = adapter
            val dialog: Dialog = builder.show()
            lv.setOnItemClickListener { _, _, pos: Int, _ ->
                val selectedCategoryCode: Int = MainActivity.allDspCategoryList[pos].code
                btnCategory.text = MainActivity.allCategoryMap[selectedCategoryCode]!!.name
                btnCategory.hint = "" + selectedCategoryCode
                dialog.dismiss()
            }
        }
        /*** amount***/
        val edtAmount = layout.findViewById<EditText>(R.id.edt_amount)
        edtAmount.addTextChangedListener(AmountTextWatcher(edtAmount))
        edtAmount.setText(java.lang.String.valueOf(item.getAmount().abs()))
        /*** memo ***/
        val edtMemo = layout.findViewById<EditText>(R.id.edt_memo)
        edtMemo.setText(item.memo)

        AlertDialog.Builder(v.context)
                .setIcon(R.mipmap.ic_mikan)
                .setTitle(v.context.getString(R.string.edit_item))
                .setView(layout)
                .setPositiveButton(R.string.save) { _, _ ->
                    val categoryCode = btnCategory.hint.toString().toInt()
                    val eventDate = UtilDate.getDBDate(btnEventDate.text.toString().split(" ")[0], MainActivity.dateFormat)
                    val updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
                    val amount = when (MainActivity.allCategoryMap[categoryCode]!!.color) {
                        UtilCategory.CATEGORY_COLOR_INCOME -> {
                            BigDecimal(edtAmount.text.toString())
                        }
                        UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                            BigDecimal(edtAmount.text.toString()).negate()
                        }
                        else -> {
                            BigDecimal(0)
                        }
                    }

                    val itemStatus = ItemStatus(
                            item.id,
                            amount,
                            UtilCurrency.CURRENCY_NONE,
                            categoryCode,
                            edtMemo.text.toString(),
                            eventDate,
                            updateDate
                    )

                    val result = UtilText.checkBeforeSave(edtAmount.text.toString())
                    if (!result.first) {
                        Toast.makeText(v.context,
                                v.context.getString(result.second),
                                Toast.LENGTH_SHORT).show()
                    } else {
                        _itemStatusViewModel.insert(itemStatus)

                        Toast.makeText(v.context,
                                v.context.getString(R.string.msg_change_successfully_saved),
                                Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }
}