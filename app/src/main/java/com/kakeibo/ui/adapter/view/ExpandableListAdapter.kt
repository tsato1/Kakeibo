//package com.kakeibo.ui.adapter.view
//
//import android.view.*
//import android.widget.*
//import androidx.recyclerview.widget.AsyncListDiffer
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.kakeibo.R
//
//class ExpandableListAdapter(private val expandableItem: ExpandableItem)
//    : RecyclerView.Adapter<ExpandableListAdapter.ViewHolder>() {
//
//    companion object {
//        const val VIEW_TYPE_PARENT = 0
//        const val VIEW_TYPE_CHILD = 1
//    }
//
//    private var isExpanded: Boolean = false
//
////    private var _expandableList: MutableList<ExpandableListRowModel> = mutableListOf()
////    private var _masterMap: SortedMap<ExpandableListRowModel.Header, List<Item>> = TreeMap()
////    fun setData(masterMap: SortedMap<ExpandableListRowModel.Header, List<Item>>, date: String) {
////        _masterMap = masterMap
////        UtilExpandableList.expandOnlySpecificDate(_masterMap, _expandableList, date)
////        notifyDataSetChanged()
////    }
//
//    private val diffCallback = object : DiffUtil.ItemCallback<ExpandableItem>() {
//        override fun areItemsTheSame(oldItem: ExpandableItem, newItem: ExpandableItem): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: ExpandableItem, newItem: ExpandableItem): Boolean {
//            return oldItem.hashCode() == newItem.hashCode()
//        }
//    }
//
//    private val differ = AsyncListDiffer(this, diffCallback)
//    var expandableItems: List<ExpandableItem>
//        get() = differ.currentList
//        set(value) = differ.submitList(value)
//
//    private var onItemClickListener: ((ExpandableItem) -> Unit)? = null
//    fun setOnItemClickListener(listener: (ExpandableItem) -> Unit) {
//        this.onItemClickListener = listener
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//
//        return when(viewType) {
//            VIEW_TYPE_PARENT -> {
//                ViewHolder.HeaderViewHolder(
//                    layoutInflater.inflate(R.layout.expandable_parent, parent, false)
//                )
////                val rowExplistParentBinding = RowExplistParentBinding.inflate(
////                                LayoutInflater.from(parent.context),
////                                parent,
////                                false)
////                ItemParentViewHolder(rowExplistParentBinding)
//            }
//            VIEW_TYPE_CHILD -> {
//                ViewHolder.ItemViewHolder(
//                    layoutInflater.inflate(R.layout.expandable_child, parent, false)
//                )
////                val rowExplistChildBinding = RowExplistChildBinding.inflate(
////                                LayoutInflater.from(parent.context),
////                                parent,
////                                false)
////                ItemChildViewHolder(rowExplistChildBinding)
//            }
//            else -> throw Exception("ExpandableAdapter: unknown item type")
//        }
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        when(holder) {
//            is ViewHolder.HeaderViewHolder -> {
//                holder.onBind(expandableItem.parent, onHeaderClinked())
//            }
//            is ViewHolder.ItemViewHolder -> {
//                holder.onBind(expandableItem.children[position - 1])
//            }
//        }
//
////        val row = _expandableList[position]
////
////        when(row.type){
////            ExpandableListRowModel.PARENT -> {
////                (holder as ItemParentViewHolder).bind(row)
////                holder.parentBinding.lnlExpListParent.setOnClickListener {
////                    if (row.isExpanded) {
////                        row.isExpanded = false
////                        holder.parentBinding.upArrow.visibility = View.GONE
////                        holder.parentBinding.closeArrow.visibility = View.VISIBLE
////                        collapseRow(position)
////                    } else {
////                        row.isExpanded = true
////                        holder.parentBinding.upArrow.visibility = View.VISIBLE
////                        holder.parentBinding.closeArrow.visibility = View.GONE
////                        expandRow(position, row.itemParent)
////                    }
////                }
////            }
////
////            ExpandableListRowModel.CHILD -> {
////                (holder as ItemChildViewHolder).bind(_lifecycleOwner, row.itemChild, _categoryViewModel)
////                holder.childBinding.rllExpListChild.setOnClickListener {
////                    val binding = DialogItemDetailBinding.inflate(LayoutInflater.from(it.context))
////                    binding.item = row.itemChild
////                    binding.categoryViewModel = _categoryViewModel
////
////                    AlertDialog.Builder(it.context)
////                            .setIcon(R.mipmap.ic_mikan)
////                            .setTitle(it.context.getString(R.string.item_detail))
////                            .setView(binding.root)
////                            .setPositiveButton(R.string.close) { _, _ -> }
////                            .setNegativeButton(R.string.delete) {_, _ -> showDeleteDialog(it, row.itemChild) }
////                            .setNeutralButton(R.string.edit) { _, _ -> showEditDialog(it, row.itemChild) }
////                            .show()
////                }
////            }
////        }
//    }
//
//    override fun getItemCount(): Int {
//        return if (isExpanded)
//            expandableItem.children.size + 1
//        else
//            1
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (position == 0)
//            VIEW_TYPE_PARENT
//        else
//            VIEW_TYPE_CHILD
//    }
//
//
//
////    private fun expandRow(position: Int, header: ExpandableListRowModel.Header) {
////        var nextPosition = position
////
////        _masterMap[header]?.let {
////            _expandableList.addAll(++nextPosition, it.map { child ->
////                ExpandableListRowModel(ExpandableListRowModel.CHILD, child)
////            })
////        }
////        notifyDataSetChanged()
////    }
////
////    private fun collapseRow(position: Int){
////        val row = _expandableList[position]
////        var nextPosition = position + 1
////
////        when (row.type) {
////            ExpandableListRowModel.PARENT -> {
////                outerloop@ while (true) {
////                    if (nextPosition == _expandableList.size
////                            || _expandableList[nextPosition].type == ExpandableListRowModel.PARENT) {
////                        break@outerloop
////                    }
////                    _expandableList.removeAt(nextPosition)
////                }
////                notifyDataSetChanged()
////            }
////        }
////    }
//
//    private fun onHeaderClinked() = View.OnClickListener {
//        isExpanded = !isExpanded
//
//        if (isExpanded) {
//            notifyItemRangeInserted(1, expandableItem.children.size)
//            notifyItemChanged(0)
//        } else {
//            notifyItemRangeRemoved(1, expandableItem.children.size)
//            notifyItemChanged(0)
//        }
//    }
//
//    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
//            private val txvHeader = itemView.findViewById<TextView>(R.id.txv_event_date)
//
//            fun onBind(parent: ExpandableItem.Parent, onClickListener: View.OnClickListener) {
//                txvHeader.text = parent.headerText
//
//                itemView.setOnClickListener {
//                    onClickListener.onClick(it)
//                }
//            }
//        }
//
//        class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
//            private val txvCategory = itemView.findViewById<TextView>(R.id.txv_category)
//
//            fun onBind(child: ExpandableItem.Child) {
//                txvCategory.text = "category code = ${child.child.categoryCode}"
//            }
//        }
//
//    }
//
////    class ItemParentViewHolder(val parentBinding: RowExplistParentBinding)
////        : RecyclerView.ViewHolder(parentBinding.root) {
////
////        fun bind(header: ExpandableListRowModel) {
////            parentBinding.header = header
////        }
////    }
////
////    class ItemChildViewHolder(val childBinding: RowExplistChildBinding)
////        : RecyclerView.ViewHolder(childBinding.root) {
////
////        fun bind(lifecycleOwner: LifecycleOwner, item: Item, categoryViewModel: CategoryViewModel) {
////            childBinding.lifecycleOwner = lifecycleOwner
////            childBinding.item = item
////            childBinding.categoryViewModel = categoryViewModel
////        }
////    }
//
////    private fun showDeleteDialog(v: View, item: Item) {
////        AlertDialog.Builder(v.context)
////                .setIcon(R.mipmap.ic_mikan)
////                .setTitle(v.context.getString(R.string.quest_do_you_want_to_delete_item))
////                .setPositiveButton(R.string.yes) { _, _ ->
////                    _itemViewModel.delete(item.id)
////                    Toast.makeText(v.context,
////                            v.context.getString(R.string.msg_item_successfully_deleted),
////                            Toast.LENGTH_SHORT).show()
////                }
////                .setNegativeButton(R.string.no, null)
////                .show()
////    }
//
////    private fun showEditDialog(v: View, item: Item) {
////        val binding = DialogItemEditBinding.inflate(LayoutInflater.from(v.context))
////        binding.lifecycleOwner = _lifecycleOwner
////        binding.itemStatus = item
////
////        /* event date */
////        val eventDates = item.eventDate.split("-")
////        val cal = GregorianCalendar(eventDates[0].toInt(), eventDates[1].toInt()-1, eventDates[2].toInt())
////        val date = cal.time
////        val dateText = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
////                Locale.getDefault()).format(date)
////                .toString() + " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
////        binding.btnEventDate.text = dateText
////        binding.btnEventDate.setOnClickListener {
////            val ymd = UtilDate.getDBDate(dateText.split(" ")[0], MainActivity.dateFormat).split("-")
////            val year = ymd[0].toInt()
////            val month = ymd[1].toInt()
////            val day = ymd[2].toInt()
////            val dialog = DatePickerDialog(v.context, { _, y, m, d ->
////                val gCal = GregorianCalendar(y, m, d)
////                val str: String = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
////                        Locale.getDefault()).format(gCal.time).toString() +
////                        " [" + MainActivity.weekNames[cal[Calendar.DAY_OF_WEEK] - 1] + "]"
////                binding.btnEventDate.text = str
////            }, year, month - 1, day)
////            dialog.show()
////        }
////        /* category */
////        binding.btnCategory.text =
////                if (item.categoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
////                    v.context.resources.getStringArray(R.array.default_category)[item.categoryCode]
////                }
////                else {
////                    _categoryViewModel.allMap.value!![item.categoryCode]!!.name
////                }
////        binding.btnCategory.hint = "" + item.categoryCode
////        binding.btnCategory.setOnClickListener {
////            /* ordered by location */
////            val adapter = CategoryListAdapter(v.context, 0, _categoryViewModel.all.value!!)
////            val builder = androidx.appcompat.app.AlertDialog.Builder(v.context)
////            val dInflater = v.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
////            val convertView: View = dInflater.inflate(R.layout.dialog_bas_search_category, null)
////            builder.setView(convertView)
////            builder.setCancelable(true)
////            builder.setIcon(R.mipmap.ic_mikan)
////            builder.setTitle(R.string.category)
////            builder.setNegativeButton(R.string.cancel) { _, _ -> }
////            val lv: ListView = convertView.findViewById(R.id.lsv_base_search_category)
////            lv.adapter = adapter
////            val dialog: Dialog = builder.show()
////            lv.setOnItemClickListener { _, _, pos: Int, _ ->
////                val selectedCategoryCode: Int = _categoryViewModel.all.value!![pos].code
////                binding.btnCategory.text =
////                        if (selectedCategoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
////                            v.context.resources.getStringArray(R.array.default_category)[selectedCategoryCode]
////                        }
////                        else {
////                            _categoryViewModel.allMap.value!![selectedCategoryCode]!!.name
////                        }
////                binding.btnCategory.hint = "" + selectedCategoryCode
////                dialog.dismiss()
////            }
////        }
////        /* amount */
////        binding.edtAmount.addTextChangedListener(AmountTextWatcher(binding.edtAmount))
////        binding.edtAmount.setText(java.lang.String.valueOf(item.amount.abs()))
////        /* memo */
////        binding.edtMemo.setText(item.memo)
////
////        AlertDialog.Builder(v.context)
////                .setIcon(R.mipmap.ic_mikan)
////                .setTitle(v.context.getString(R.string.edit_item))
////                .setView(binding.root)
////                .setPositiveButton(R.string.save) { _, _ ->
////                    val categoryCode = binding.btnCategory.hint.toString().toInt()
////                    val eventDate = UtilDate.getDBDate(binding.btnEventDate.text.toString().split(" ")[0], MainActivity.dateFormat)
////                    val updateDate = UtilDate.getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
////                    val amount = when (_categoryViewModel.allMap.value!![categoryCode]!!.color) {
////                        UtilCategory.CATEGORY_COLOR_INCOME -> {
////                            BigDecimal(binding.edtAmount.text.toString())
////                        }
////                        UtilCategory.CATEGORY_COLOR_EXPENSE -> {
////                            BigDecimal(binding.edtAmount.text.toString()).negate()
////                        }
////                        else -> {
////                            BigDecimal(0)
////                        }
////                    }
////
////                    val itemStatus = Item(
////                            item.id,
////                            amount,
////                            UtilCurrency.CURRENCY_NONE,
////                            categoryCode,
////                            binding.edtMemo.text.toString(),
////                            eventDate,
////                            updateDate
////                    )
////
////                    val result = UtilText.checkBeforeSave(binding.edtAmount.text.toString())
////                    if (!result.first) {
////                        Toast.makeText(v.context,
////                                v.context.getString(result.second),
////                                Toast.LENGTH_SHORT).show()
////                    } else {
////                        _itemViewModel.insert(itemStatus)
////
////                        Toast.makeText(v.context,
////                                v.context.getString(R.string.msg_change_successfully_saved),
////                                Toast.LENGTH_SHORT).show()
////                    }
////                }
////                .setNegativeButton(R.string.cancel, null)
////                .show()
////    }
//}