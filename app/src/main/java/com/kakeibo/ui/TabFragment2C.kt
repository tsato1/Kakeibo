package com.kakeibo.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.FragmentReportCBinding
import com.kakeibo.export.ExportActivity
import com.kakeibo.ui.model.Balance
import com.kakeibo.ui.model.ItemDetailListAdapter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class TabFragment2C : Fragment(), OnChartValueSelectedListener {
    private var _activity: Activity? = null
    private var _stringBuilder: StringBuilder? = null
    private var _balance: Balance? = null
    private var _horizontalBarChart: HorizontalBarChart? = null
    private var _inPieGraph: PieGraph? = null
    private var _exPieGraph: PieGraph? = null
    private var _itemCategoryInList: MutableList<ItemStatus>? = null
    private var _itemCategoryExList: MutableList<ItemStatus>? = null
    private var _itemListInAdapter: ItemDetailListAdapter? = null
    private var _itemListExAdapter: ItemDetailListAdapter? = null
    private var _lsvCategoryIn: ListView? = null
    private var _lsvCategoryEx: ListView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _activity = activity
//        _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits)
//        val billingViewModel = ViewModelProviders.of(requireActivity())[BillingViewModel::class.java]
//        val subscriptionViewModel = ViewModelProviders.of(requireActivity())[SubscriptionStatusViewModel::class.java]
//        val categoryStatusViewModel = ViewModelProviders.of(requireActivity())[CategoryStatusViewModel::class.java]
        val fragmentBinding: FragmentReportCBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_c, container, false)
//        fragmentBinding.lifecycleOwner = this
//        fragmentBinding.billingViewModel = billingViewModel
//        fragmentBinding.subscriptionViewModel = subscriptionViewModel
//        fragmentBinding.categoryStatusViewModel = categoryStatusViewModel
        val view = fragmentBinding.root
//        _horizontalBarChart = view.findViewById(R.id.horizontal_bar_chart)
//        _inPieGraph = view.findViewById(R.id.pie_graph_income)
//        _exPieGraph = view.findViewById(R.id.pie_graph_expense)
//        _lsvCategoryIn = view.findViewById(R.id.lsv_income)
//        _lsvCategoryEx = view.findViewById(R.id.lsv_expense)
//        setUpGraphes()
        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        val bundle = this.arguments
        if (bundle != null) {
            _query = bundle.getParcelable("query")
        }
        loadItemsOrderByCategory()
        /*** <- to handle come back from settings  */
    }

    //    @Override
    //    public void onPause() {
    //        super.onPause();
    //        UtilKeyboard.hideSoftKeyboard(_activity);
    //    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpGraphes() {
        _horizontalBarChart!!.setDrawBarShadow(false)
        _horizontalBarChart!!.setDrawValueAboveBar(true)
        _horizontalBarChart!!.setDrawGridBackground(false)
        _horizontalBarChart!!.setMaxVisibleValueCount(2) //income and expense
        _horizontalBarChart!!.setFitBars(true)
        _horizontalBarChart!!.isHighlightPerDragEnabled = false
        _horizontalBarChart!!.isHighlightPerTapEnabled = false
        _horizontalBarChart!!.isDoubleTapToZoomEnabled = false
        _horizontalBarChart!!.setNoDataTextColor(R.color.colorBlack)
        _horizontalBarChart!!.setPinchZoom(false)
        _horizontalBarChart!!.description.isEnabled = false
        _horizontalBarChart!!.setOnChartValueSelectedListener(this)
        _horizontalBarChart!!.animateY(resources.getInteger(R.integer.chart_animation_milli_seconds))
        val xl = _horizontalBarChart!!.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.granularity = 10f
        xl.isEnabled = false
        val yl = _horizontalBarChart!!.axisLeft
        yl.setDrawAxisLine(true)
        yl.setDrawGridLines(true)
        yl.axisMinimum = 0f // this replaces setStartAtZero(true)
        val yr = _horizontalBarChart!!.axisRight
        yr.setDrawAxisLine(true)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f // this replaces setStartAtZero(true)
        val l = _horizontalBarChart!!.legend
        //        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setFormSize(8f);
//        l.setXEntrySpace(5f);
        l.isEnabled = false
        _inPieGraph!!.thickness = PIE_GRAPH_THICKNESS
        _exPieGraph!!.thickness = PIE_GRAPH_THICKNESS
        ViewCompat.setNestedScrollingEnabled(_lsvCategoryIn!!, true)
        ViewCompat.setNestedScrollingEnabled(_lsvCategoryEx!!, true)
        _lsvCategoryIn!!.setOnTouchListener { v: View, event: MotionEvent ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN ->                         // Disallow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                         // Allow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            // Handle ListView touch events.
            v.onTouchEvent(event)
            true
        }
        _lsvCategoryEx!!.setOnTouchListener { v: View, event: MotionEvent ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN ->                     // Disallow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP ->                     // Allow ScrollView to intercept touch events.
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }

            // Handle ListView touch events.
            v.onTouchEvent(event)
            true
        }
        _lsvCategoryIn!!.onItemClickListener = ItemClickListener()
        _lsvCategoryEx!!.onItemClickListener = ItemClickListener()
        _stringBuilder = StringBuilder()
        _itemCategoryInList = ArrayList()
        _itemCategoryExList = ArrayList()
        _itemListInAdapter = ItemDetailListAdapter(_activity!!, 0, _itemCategoryInList)
        _itemListExAdapter = ItemDetailListAdapter(_activity!!, 0, _itemCategoryExList)
        _lsvCategoryIn!!.adapter = _itemListInAdapter
        _lsvCategoryEx!!.adapter = _itemListExAdapter
    }

    private fun setData() {
        val outValue = TypedValue()
        resources.getValue(R.dimen.horizontal_bar_bar_width, outValue, true)
        val barWidth = outValue.float
        resources.getValue(R.dimen.horizontal_bar_bar_space, outValue, true)
        val spaceForBar = outValue.float
        val values = ArrayList<BarEntry>()
        values.add(BarEntry(0 * spaceForBar, _balance!!.income.toFloat()))
        values.add(BarEntry(1 * spaceForBar, _balance!!.expense.toFloat()))
        val income: BarDataSet
        val expense: BarDataSet
        if (_horizontalBarChart!!.data != null && _horizontalBarChart!!.data.dataSetCount > 0) {
            _horizontalBarChart!!.invalidate()
            income = _horizontalBarChart!!.data.getDataSetByIndex(0) as BarDataSet
            income.values = values
            expense = _horizontalBarChart!!.data.getDataSetByIndex(1) as BarDataSet
            expense.values = values
            _horizontalBarChart!!.data.notifyDataChanged()
            _horizontalBarChart!!.notifyDataSetChanged()
        } else {
            income = BarDataSet(values, getString(R.string.income))
            expense = BarDataSet(values, getString(R.string.expense_colon))
            income.setDrawIcons(false)
            expense.setDrawIcons(false)
            expense.setColors(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.colorAccent))
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(income)
            dataSets.add(expense)
            val data = BarData(dataSets)
            resources.getValue(R.dimen.horizontal_bar_text_size, outValue, true)
            data.setValueTextSize(outValue.float)
            data.barWidth = barWidth
            _horizontalBarChart!!.data = data
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {
        _horizontalBarChart!!.getBarBounds(e as BarEntry, RectF())
        val position = _horizontalBarChart!!.getPosition(e, _horizontalBarChart!!.data.getDataSetByIndex(h.dataSetIndex)
                .axisDependency)
        val popup = PopupWindow(_activity)
        val layout = layoutInflater.inflate(R.layout.popup_bar_chart, null)
        popup.contentView = layout
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        popup.showAsDropDown(_horizontalBarChart)
        //        popup.showAsDropDown(_view);
        MPPointF.recycleInstance(position)
    }

    override fun onNothingSelected() {}
    internal inner class ItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            val lsvCat = parent as ListView
            val tmp = lsvCat.getItemAtPosition(position) as ItemStatus
            val searchResultList: MutableList<ItemStatus> = ArrayList()
            searchResultList.clear()
            val queries = _query!!.queryCs
            Log.d(TAG, "loadItems: " + queries[tmp.categoryCode])

//            _itemDbAdapter.open();
//            Cursor c = _itemDbAdapter.getItemsByRawQuery(queries.get(tmp.getCategoryCode()));
//
//            if (c.moveToFirst()) {
//                do {
//                    ItemStatus itemStatus = new ItemStatus(
//                            c.getInt(c.getColumnIndex(ItemDBAdapter.COL_ID)),
//                            c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
//                            "",
//                            _fractionDigits,
//                            c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
//                            c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
//                    );
//
//                    searchResultList.add(itemStatus);
//                } while (c.moveToNext());
//            }
//            _itemDbAdapter.close();
//
//            CategoryDetailListAdapter categoryDetailListAdapter =
//                    new CategoryDetailListAdapter(_activity, 0, searchResultList);
//            ListView listView = new ListView(_activity);
//            listView.setAdapter(categoryDetailListAdapter);
//            AlertDialog.Builder dialog = new AlertDialog.Builder(_activity);
//            dialog.setIcon(R.mipmap.ic_mikan);
//            dialog.setTitle(UtilCategory.getCategoryStr(getContext(), tmp.getCategoryCode()));
//            dialog.setPositiveButton(R.string.ok, (DialogInterface d, int which) -> { });
//            dialog.setView(listView).create();
//            dialog.show();
        }
    }

    fun setQuery(query: Query?) {
        _query = query
    }

    fun loadItemsOrderByCategory() {
        Log.d(TAG, "loadItemsOrderByCategory() " + _query!!.queryC)
        _balance = Balance.newInstance(_fractionDigits)
        _itemCategoryInList!!.clear()
        _itemCategoryExList!!.clear()
        //        _itemDbAdapter.open();
//        Cursor c = _itemDbAdapter.getItemsByRawQuery(_query.getQueryC());
//
//        if (c!=null && c.moveToFirst()) {
//            BigDecimal balanceDay = new BigDecimal(0)
//                    .setScale(_fractionDigits, RoundingMode.UNNECESSARY);
//
//            do {
//                ItemStatus itemStatus = new ItemStatus(
//                        0,
//                        c.getLong(c.getColumnIndex("SUM(amount)")),
//                        "",
//                        _fractionDigits,
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                        "",
//                        "",
//                        ""
//                );
//
//                if (UtilCategory.getCategoryColor(_activity, item.getCategoryCode())==UtilCategory.CATEGORY_COLOR_INCOME) {
////todo should be disposable                if(c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)) == 0) {
//                    _balance.addIncome(itemStatus.getAmount());
//                    balanceDay = balanceDay.add(itemStatus.getAmount());
//                    _itemCategoryInList.add(itemStatus);
//                } else if (UtilCategory.getCategoryColor(_activity, itemStatus.getCategoryCode())==UtilCategory.CATEGORY_COLOR_EXPENSE) {
//                    _balance.addExpense(itemStatus.getAmount());
//                    balanceDay = balanceDay.subtract(itemStatus.getAmount());
//                    _itemCategoryExList.add(itemStatus);
//                }
//            } while (c.moveToNext());
//        }
//
//        _itemDbAdapter.close();
        _itemListInAdapter!!.notifyDataSetChanged()
        _itemListExAdapter!!.notifyDataSetChanged()
        adjustItemCategoryListViewHeight()
        calculatePercentage()
        makePieGraph()
        setData()
        _itemLoadListener!!.onItemsLoaded(_balance!!)
    }

    private fun adjustItemCategoryListViewHeight() {
        val lpmsIncome = _lsvCategoryIn!!.layoutParams
        val lpmsExpens = _lsvCategoryEx!!.layoutParams
        val rowHeightIncome = resources.getDimension(R.dimen.item_category_list_row_height).toInt() + _itemCategoryInList!!.size
        lpmsIncome.height = _itemCategoryInList!!.size * rowHeightIncome
        _lsvCategoryIn!!.layoutParams = lpmsIncome
        val rowHeightExpens = resources.getDimension(R.dimen.item_category_list_row_height).toInt() + _itemCategoryExList!!.size
        lpmsExpens.height = _itemCategoryExList!!.size * rowHeightExpens
        _lsvCategoryEx!!.layoutParams = lpmsExpens
    }

    private fun calculatePercentage() {
        val sumIn = _balance!!.income
        val sumEx = _balance!!.expense
        for (i in _itemCategoryInList!!.indices) {
            val percentage = _itemCategoryInList!![i].getAmount()
                    .multiply(BigDecimal(100))
                    .divide(sumIn, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN)
            _itemCategoryInList!![i].memo = percentage.toString() // memo is place holder for percentage
        }
        for (i in _itemCategoryExList!!.indices) {
            val percentage = _itemCategoryExList!![i].getAmount()
                    .multiply(BigDecimal(100))
                    .divide(sumEx, RoundingMode.HALF_EVEN)
                    .setScale(0, RoundingMode.DOWN)
            _itemCategoryExList!![i].memo = percentage.toString() // memo is place holder for percentage
        }
    }

    private fun makePieGraph() {
        _inPieGraph!!.removeSlices()
        _exPieGraph!!.removeSlices()
        var inPieSlice: PieSlice
        var exPieSlice: PieSlice
        for (i in _itemCategoryExList!!.indices) {
            exPieSlice = PieSlice()
            exPieSlice.color = Color.parseColor(Constants.CATEGORY_COLORS[i])
            exPieSlice.value = _itemCategoryExList!![i].getAmount().toFloat()
            _exPieGraph!!.addSlice(exPieSlice)
        }
        for (i in _itemCategoryInList!!.indices) {
            inPieSlice = PieSlice()
            inPieSlice.color = ContextCompat.getColor(_activity!!, R.color.colorPrimary)
            inPieSlice.value = _itemCategoryInList!![i].getAmount().toFloat()
            _inPieGraph!!.addSlice(inPieSlice)
        }
    }

    fun export() {
        Log.d(TAG, "export() called")
        if (_itemCategoryExList!!.isEmpty() || _itemCategoryInList!!.isEmpty()) {
            Toast.makeText(_activity, R.string.nothing_to_export, Toast.LENGTH_SHORT).show()
            return
        }
        val dialogExport = AlertDialog.Builder(_activity)
        dialogExport.setIcon(R.mipmap.ic_mikan)
        dialogExport.setTitle(getString(R.string.export_category))
        dialogExport.setMessage(getString(R.string.quest_export_this_report_C))
        dialogExport.setPositiveButton(R.string.yes) { dialog, which ->
            val rblSaveToFile = Runnable {
                println("Runnable running")
                queryToSaveLocal()
            }
            val thread = Thread(rblSaveToFile)
            thread.start()
            val intent = Intent(_activity, ExportActivity::class.java)
            intent.putExtra("REPORT_VIEW_TYPE", TabFragment2.REPORT_BY_CATEGORY)
            startActivity(intent)
        }
        dialogExport.show()
    }

    private fun queryToSaveLocal() {
        /***
         * expecting: queryD=
         * SELECT * FROM ITEMS WHERE strftime('%Y-%m', event_date) = '2018-11' ORDER BY event_date ASC
         */
        val query = _query!!.queryD!!
                .replace("ORDER BY event_date ASC", " ORDER BY category_code, amount DESC")
        Log.d(TAG, "queryToSaveLocal() $query")
        _stringBuilder!!.setLength(0)
        _stringBuilder!!.append(resources.getString(R.string.category))
        _stringBuilder!!.append(",")
        _stringBuilder!!.append(resources.getString(R.string.amount))
        _stringBuilder!!.append(",")
        _stringBuilder!!.append(resources.getString(R.string.memo))
        _stringBuilder!!.append(",")
        _stringBuilder!!.append(resources.getString(R.string.event_date))
        _stringBuilder!!.append(",")
        _stringBuilder!!.append(resources.getString(R.string.updated_date))
        _stringBuilder!!.append("\n")

//        _itemDbAdapter.open();
//        Cursor c = _itemDbAdapter.getItemsByRawQuery(query);
//
//        if (c!=null && c.moveToFirst()) {
//            do {
//                ItemStatus itemStatus = new ItemStatus(
//                        0,
//                        c.getLong(c.getColumnIndex(ItemDBAdapter.COL_AMOUNT)),
//                        "",
//                        _fractionDigits,
//                        c.getInt(c.getColumnIndex(ItemDBAdapter.COL_CATEGORY_CODE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_MEMO)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_EVENT_DATE)),
//                        c.getString(c.getColumnIndex(ItemDBAdapter.COL_UPDATE_DATE))
//                );
//
//                _stringBuilder.append(UtilCategory.getCategoryStr(getContext(), itemStatus.getCategoryCode()));
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getAmount());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getMemo());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getEventDate());
//                _stringBuilder.append(",");
//                _stringBuilder.append(itemStatus.getUpdateDate());
//                _stringBuilder.append("\n");
//            } while (c.moveToNext());
//        }
//
//        UtilFiles.writeToFile(ExportActivity.FILE_ORDER_CATEGORY,
//                _stringBuilder.toString(), _activity, Context.MODE_PRIVATE);

        //todo tell the CreateFileInFolderActivity that it's ready to upload
    }

    companion object {
        private val TAG = TabFragment2C::class.java.simpleName
        private const val PIE_GRAPH_THICKNESS = 120
        private var _query: Query? = null
        private var _itemLoadListener: ItemLoadListener? = null
        private var _fractionDigits = 0
        fun newInstance(itemLoadListener: ItemLoadListener, query: Query?): TabFragment2C {
            val tabFragment2C = TabFragment2C()
            val args = Bundle()
            args.putParcelable("query", query)
            _query = query
            _itemLoadListener = itemLoadListener
            tabFragment2C.arguments = args
            return tabFragment2C
        }
    }
}