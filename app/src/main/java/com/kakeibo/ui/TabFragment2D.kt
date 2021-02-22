//package com.kakeibo.ui
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.app.DatePickerDialog
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.text.SpannableString
//import android.text.TextUtils
//import android.text.style.ForegroundColorSpan
//import android.util.Log
//import android.view.*
//import android.view.ContextMenu.ContextMenuInfo
//import android.view.View.OnCreateContextMenuListener
//import android.widget.*
//import android.widget.ExpandableListView.ExpandableListContextMenuInfo
//import android.widget.ExpandableListView.OnChildClickListener
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.kakeibo.R
//import com.kakeibo.SubApp
//import com.kakeibo.data.ItemStatus
//import com.kakeibo.ui.ExportActivity
//import com.kakeibo.ui.model.ExpandableListAdapter
//import com.kakeibo.util.UtilCurrency.checkAmount
//import com.kakeibo.util.UtilDate
//import com.kakeibo.util.UtilDate.convertDateFormat
//import com.kakeibo.util.UtilDate.getDateWithDayFromDBDate
//import java.text.SimpleDateFormat
//import java.util.*
//
//class TabFragment2D : Fragment() {
//    private var _activity: Activity? = null
//    private var _view: View? = null
//    private var _stringBuilder: StringBuilder? = null
//    private var lstDateHeader: List<String>? = null
//    private var hmpChildData: HashMap<String, List<ItemStatus>>? = null
//    private var elaData: ExpandableListAdapter? = null
//    private var explvData: ExpandableListView? = null
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        _activity = activity
//        _weekNames = resources.getStringArray(R.array.week_name)
//        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
//        _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits)
//        _numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns)
//        _view = inflater.inflate(R.layout.fragment_tab_2d, container, false)
//        findViews()
//        return _view
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume() called")
//        val bundle = this.arguments
//        if (bundle != null) {
//            _query = bundle.getParcelable("query")
//        }
//        loadItemsOrderByDate()
//        /*** <- to handle come back from settings  */
//    }
//
//    //    @Override
//    //    public void onPause() {
//    //        super.onPause();
//    //        UtilKeyboard.hideKeyboard(_activity);
//    //    }
//    fun findViews() {
//        explvData = _view!!.findViewById(R.id.lsv_expandable)
//        explvData!!.setOnChildClickListener(ChildClickListener())
//        explvData!!.setOnCreateContextMenuListener(ChildClickContextMenuListener())
//        _stringBuilder = StringBuilder()
//        lstDateHeader = ArrayList()
//        hmpChildData = HashMap()
//        elaData = ExpandableListAdapter(_activity!!, lstDateHeader!!, hmpChildData!!)
//        explvData!!.setAdapter(elaData)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    internal inner class ChildClickListener : OnChildClickListener {
//        override fun onChildClick(parent: ExpandableListView, view: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
//            val child = elaData!!.getChild(groupPosition, childPosition)
//            val item = child as ItemStatus
//            val inflater = _activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val layout = inflater.inflate(R.layout.dialog_item_detail, view.findViewById(R.id.layout_root))
//            val txvCategory = layout.findViewById<TextView>(R.id.txv_detail_category)
//            val txvAmount = layout.findViewById<TextView>(R.id.txv_detail_amount)
//            val txvMemo = layout.findViewById<TextView>(R.id.txv_detail_memo)
//            val txvRegistrationDate = layout.findViewById<TextView>(R.id.txv_detail_registration)
//
////            String categoryText = getString(R.string.category_colon) + UtilCategory.getCategoryStr(getContext(), item.getCategoryCode());
//            val categoryText = ""
//            txvCategory.text = categoryText
//            val span1: SpannableString
//            val span2: SpannableString
//            if (item.categoryCode <= 0) {
//                span1 = SpannableString(getString(R.string.amount_colon))
//                span2 = SpannableString("+" + item.getAmount())
//                span2.setSpan(ForegroundColorSpan(ContextCompat.getColor(_activity!!, R.color.colorBlue)), 0, 1, 0)
//            } else {
//                span1 = SpannableString(getString(R.string.amount_colon))
//                span2 = SpannableString("-" + item.getAmount())
//                span2.setSpan(ForegroundColorSpan(ContextCompat.getColor(_activity!!, R.color.colorRed)), 0, 1, 0)
//            }
//            txvAmount.text = TextUtils.concat(span1, span2)
//            val memoText = getString(R.string.memo_colon) + item.memo
//            txvMemo.text = memoText
//            val savedOnText = getString(R.string.updated_on_colon) +
//                    getDateWithDayFromDBDate(item.updateDate, _weekNames!!, _dateFormat)
//            txvRegistrationDate.text = savedOnText
//            AlertDialog.Builder(_activity)
//                    .setIcon(R.mipmap.ic_mikan)
//                    .setTitle(resources.getString(R.string.item_detail))
//                    .setView(layout)
//                    .show()
//            return false
//        }
//    }
//
//    internal inner class ChildClickContextMenuListener : OnCreateContextMenuListener {
//        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
//            val info = menuInfo as ExpandableListContextMenuInfo
//            val type = ExpandableListView.getPackedPositionType(info.packedPosition)
//            if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//                menu.setHeaderIcon(R.mipmap.ic_mikan)
//                menu.add(0, MENU_ITEM_ID_EDIT, 0, R.string.edit)
//                menu.add(0, MENU_ITEM_ID_DELETE, 1, R.string.delete)
//            }
//        }
//    }
//
//    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
//        val info = menuItem.menuInfo as ExpandableListContextMenuInfo
//        val groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition)
//        val childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition)
//        val item = elaData!!.getChild(groupPosition, childPosition) as ItemStatus
//        when (menuItem.itemId) {
//            MENU_ITEM_ID_DELETE -> {
//                AlertDialog.Builder(_activity)
//                        .setIcon(R.mipmap.ic_mikan)
//                        .setTitle(getString(R.string.quest_do_you_want_to_delete_item))
//                        .setPositiveButton(R.string.yes) { dialog, which ->
//                            //Log.d("groupPosition", String.valueOf(groupPosition));
//                            //Log.d("childPosition", String.valueOf(childPosition));
//                            //                                _itemDbAdapter.open();
//                            //                                final int itemId = item.getId();
//                            //
//                            //                                if(_itemDbAdapter.deleteItem(itemId)) {
//                            //                                    Toast.makeText(_activity, getString(R.string.msg_item_successfully_deleted), Toast.LENGTH_SHORT).show();
//                            //                                }
//                            //
//                            //                                loadItemsOrderByDate();
//                            //                                _itemDbAdapter.close();
//                        }
//                        .setNegativeButton(R.string.no, null)
//                        .show()
//                return true
//            }
//            MENU_ITEM_ID_EDIT -> {
//                val layoutInflater = _activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                val layout = layoutInflater.inflate(R.layout.dialog_item_edit, _view!!.findViewById(R.id.layout_root))
//
//                /*** event date  */
//                val btnEventDate = layout.findViewById<Button>(R.id.btn_event_date)
//                val eventDates = item.eventDate.split("-").toTypedArray()
//                val cal = GregorianCalendar(eventDates[0].toInt(),
//                        eventDates[1].toInt() - 1, eventDates[2].toInt())
//                val date = cal.time
//                val eventDate = (SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
//                        Locale.getDefault()).format(date)
//                        + " [" + _weekNames!![cal[Calendar.DAY_OF_WEEK] - 1] + "]")
//                btnEventDate.text = eventDate
//                btnEventDate.setOnClickListener { v1: View? ->
//                    val ymd = convertDateFormat(
//                            eventDate.split("\\s+").toTypedArray()[0], _dateFormat, 3).split("-").toTypedArray()
//                    val year = ymd[0].toInt()
//                    val month = ymd[1].toInt()
//                    val day = ymd[2].toInt()
//                    val dialog = DatePickerDialog(_activity!!, { picker, year, month, day ->
//                        val cal = GregorianCalendar(year, month, day)
//                        val date = cal.time
//                        val str = (SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
//                                Locale.getDefault()).format(date)
//                                + " [" + _weekNames!![cal[Calendar.DAY_OF_WEEK] - 1] + "]")
//                        btnEventDate.text = str
//                    }, year, month - 1, day)
//                    dialog.show()
//                }
//            }
//        }
//        return super.onContextItemSelected(menuItem)
//    }
//
//    /*** same functionality is in TabFragment1 too  */
//    private fun checkBeforeSave(edtAmount: EditText): Boolean {
//        if ("" == edtAmount.text.toString()) {
//            Toast.makeText(activity, resources.getString(R.string.err_please_enter_amount), Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if ("0" == edtAmount.text.toString() || "0.0" == edtAmount.text.toString() || "0.00" == edtAmount.text.toString() || "0.000" == edtAmount.text.toString()) {
//            Toast.makeText(activity, resources.getString(R.string.err_amount_cannot_be_0), Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (!checkAmount(edtAmount.text.toString())) {
//            Toast.makeText(activity, R.string.err_amount_invalid, Toast.LENGTH_SHORT).show()
//            return false
//        }
//        return true
//    }
//
//    fun setQuery(query: Query?) {
//        _query = query
//    }
//
//    fun loadItemsOrderByDate() {
////        Log.d(TAG, "loadItemsOrderByDate() " + _query.getQueryD());
////
////        lstDateHeader.clear();
////        hmpChildData.clear();
////        Balance balance = Balance.newInstance(_fractionDigits);
////        int sameDateCounter = 0;
////        _stringBuilder.setLength(0);
////        _stringBuilder.append(getResources().getString(R.string.event_date));
////        _stringBuilder.append(",");
////        _stringBuilder.append(getResources().getString(R.string.amount));
////        _stringBuilder.append(",");
////        _stringBuilder.append(getResources().getString(R.string.category));
////        _stringBuilder.append(",");
////        _stringBuilder.append(getResources().getString(R.string.memo));
////        _stringBuilder.append(",");
////        _stringBuilder.append(getResources().getString(R.string.updated_date));
////        _stringBuilder.append("\n");
//
////        _itemDbAdapter.open();
////
////        Cursor c = _itemDbAdapter.getItemsByRawQuery(_query.getQueryD());
////
////        if (c!=null && c.moveToFirst()) {
////            String eventDate = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE));
////            BigDecimal balanceDay = new BigDecimal(0)
////                    .setScale(_fractionDigits, RoundingMode.UNNECESSARY);;
////            List<Item> tmpItemList = new ArrayList<>();
////
////            do {
////                if (!c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)).equals(eventDate)){ // if the event day of an item increases
////                    lstDateHeader.add(eventDate.replace('-', ',') + "," + balanceDay); // comma is deliminator
////                    hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList); // set the header of the old day
////                    balanceDay = BigDecimal.valueOf(0);
////                    /*** change of the date ***/
////                    eventDate = c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)); // set a new date
////                    tmpItemList = new ArrayList<>(); // empty the array list of items
////                    sameDateCounter++;
////                }
////
////                Item item = new Item(
////                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID)),
////                        c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
////                        "",
////                        _fractionDigits,
////                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
////                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
////                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
////                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
////                );
////
////                if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
//////todo should be disposable //                if(c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)) == 0) {
////                    balance.addIncome(item.getAmount());
////                    balanceDay = balanceDay.add(item.getAmount());
////                } else if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
////                    balance.addExpense(item.getAmount());
////                    balanceDay = balanceDay.subtract(item.getAmount());
////                }
////
////                _stringBuilder.append(item.getEventDate());
////                _stringBuilder.append(",");
////                _stringBuilder.append(item.getAmount());
////                _stringBuilder.append(",");
////                _stringBuilder.append(UtilCategory.getCategoryStr(getContext(), item.getCategoryCode()));
////                _stringBuilder.append(",");
////                _stringBuilder.append(item.getMemo());
////                _stringBuilder.append(",");
////                _stringBuilder.append(item.getUpdateDate());
////                _stringBuilder.append("\n");
////
////                tmpItemList.add(item);
////            } while (c.moveToNext());
////
////            lstDateHeader.add(eventDate.replace('-', ',') + "," + balanceDay); // set what to show on the header
////            hmpChildData.put(lstDateHeader.get(sameDateCounter), tmpItemList);
////        }
////
////        _itemDbAdapter.close();
////        elaData.notifyDataSetChanged();
////
////        UtilFiles.writeToFile(ExportActivity.FILE_ORDER_DATE, _stringBuilder.toString(),
////                _activity, Context.MODE_PRIVATE);
////        _stringBuilder.setLength(0);
////
////        _itemLoadListener.onItemsLoaded(balance);
//    }
//
//    fun focusOnSavedItem(eventDate: String) {
//        Log.d(TAG, "focusOnSavedItem() $eventDate")
//        val m = eventDate.split("-").toTypedArray()[1].toInt()
//        val d = eventDate.split("-").toTypedArray()[2].toInt()
//        for (i in lstDateHeader!!.indices) {
//            val header = lstDateHeader!![i].split("[,]").toTypedArray() // ex. "2018,04,30,-700"
//            if (header[1].toInt() == m && header[2].toInt() == d) {
//                explvData!!.expandGroup(i)
//                explvData!!.smoothScrollToPositionFromTop(i, 0)
//            } else {
//                explvData!!.collapseGroup(i)
//            }
//        }
//    }
//
//    fun export() {
//        Log.d(TAG, "export() called")
//        if (lstDateHeader!!.size == 0 || hmpChildData!!.size == 0) {
//            Toast.makeText(_activity, R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
//            return
//        }
//        val dialogExport = AlertDialog.Builder(_activity)
//        dialogExport.setIcon(R.mipmap.ic_mikan)
//        dialogExport.setTitle(getString(R.string.export_date))
//        dialogExport.setMessage(getString(R.string.quest_export_this_report_D))
//        dialogExport.setPositiveButton(R.string.yes) { dialog, which ->
//            val intent = Intent(_activity, ExportActivity::class.java)
//            intent.putExtra("REPORT_VIEW_TYPE", TabFragment2.REPORT_BY_DATE)
//            startActivityForResult(intent, 10)
//        }
//        dialogExport.show()
//    }
//
//    companion object {
//        val TAG = TabFragment2D::class.java.simpleName
//        private const val MENU_ITEM_ID_DELETE = 0
//        private const val MENU_ITEM_ID_EDIT = 1
//        private var _query: Query? = null
//        private var _itemLoadListener: ItemLoadListener? = null
//        private var _weekNames: Array<String>? = null
//        private var _dateFormat = 0
//        private var _fractionDigits = 0
//        private var _numColumns = 0
//        fun newInstance(itemLoadListener: ItemLoadListener?, query: Query?): TabFragment2D {
//            val tabFragment2D = TabFragment2D()
//            val args = Bundle()
//            args.putParcelable("query", query)
//            _query = query
//            _itemLoadListener = itemLoadListener
//            tabFragment2D.arguments = args
//            return tabFragment2D
//        }
//    }
//}