package com.kakeibo.ui

import ItemDBAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.toColorInt
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import com.kakeibo.Constants
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.databinding.FragmentReportBinding
import com.kakeibo.ui.adapter.ExpandableListAdapter
import com.kakeibo.ui.adapter.ReportCListAdapter
import com.kakeibo.ui.listener.ItemLoadListener
import com.kakeibo.ui.model.Balance
import com.kakeibo.ui.model.ExpandableListRowModel
import com.kakeibo.ui.model.Medium
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.util.QueryBuilder
import com.kakeibo.util.QueryBuilder.build
import com.kakeibo.util.QueryBuilder.init
import com.kakeibo.util.QueryBuilder.setCGroupBy
import com.kakeibo.util.QueryBuilder.setCOrderBy
import com.kakeibo.util.QueryBuilder.setCsWhere
import com.kakeibo.util.QueryBuilder.setDOrderBy
import com.kakeibo.util.QueryBuilder.setDate
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.convertMtoMM
import com.kakeibo.util.UtilDate.getTodaysDate
import java.math.BigDecimal
import javax.annotation.Nonnull


/**
 * Created by T on 2015/09/14.
 */
class TabFragment2 : Fragment(), ItemLoadListener {

    companion object {
        private val TAG = TabFragment2::class.java.simpleName

        private const val SWIPE_REFRESH_MILLI_SECOND = 400
        var REPORT_BY_DATE = 0
        var REPORT_BY_CATEGORY = 1
        var REPORT_BY_AMOUNT = 2 //todo
        private var _dateFormat = 0
        private var _weekNames: Array<String>? = null
        private var _query: Query? = null
        private var _calMonth = 0
        private var _calYear = 0
        private var _eventDate: String? = null

        fun newInstance(): TabFragment2 {
            val tabFragment2 = TabFragment2()
            val args = Bundle()
            tabFragment2.arguments = args
            return tabFragment2
        }
    }

    private var _activity: Activity? = null
    private var _context: Context? = null
    private var srlReload: SwipeRefreshLayout? = null
    private var btnClose: ImageButton? = null
    private var _itemStatusViewModel: ItemStatusViewModel? = null

    override fun onAttach(@Nonnull context: Context) {
        super.onAttach(context)
        _context = context

        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        val todaysDate = getTodaysDate(UtilDate.DATE_FORMAT_DB)
        val ymd = todaysDate.split("-").toTypedArray()
        _calYear = ymd[0].toInt()
        _calMonth = ymd[1].toInt()

        /*** making query  */
        _query = Query(Query.QUERY_TYPE_NEW)
        init(ArrayList())
        setDate(todaysDate, "")
        setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
        setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC)
        setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
        setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC)
        build(_query!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _activity = activity
        _weekNames = resources.getStringArray(R.array.week_name)

        _itemStatusViewModel = ViewModelProviders.of(requireActivity())[ItemStatusViewModel::class.java]
        val fragmentBinding: FragmentReportBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_report, container, false)
        fragmentBinding.lifecycleOwner = this
        fragmentBinding.itemStatusViewModel = _itemStatusViewModel
        val view = fragmentBinding.root

        val reportCBinding = fragmentBinding.fragmentReportC
        reportCBinding.itemStatusViewModel = _itemStatusViewModel

        val bannerDatePickerBinding = fragmentBinding.bannerDatePicker
        bannerDatePickerBinding.medium = MainActivity.medium

        val incomePieGraph: PieGraph = view.findViewById(R.id.pie_graph_income)
        val expensePieGraph: PieGraph = view.findViewById(R.id.pie_graph_expense)

        val expandableListView: RecyclerView = view.findViewById(R.id.lsv_expandable)
        val expandableList: MutableList<ExpandableListRowModel> = ArrayList()
        val expandableListAdapter = ExpandableListAdapter(expandableList, _itemStatusViewModel!!)
        expandableListView.adapter = expandableListAdapter
        expandableListView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        _itemStatusViewModel!!.allByDate.observe(viewLifecycleOwner, { map ->
            expandableList.clear()
            expandableListAdapter.setMasterMap(map.toSortedMap(
                    compareBy<Pair<String, BigDecimal>> { it.first }.thenBy { it.second })
            )
        })

        val incomeListView: RecyclerView = view.findViewById(R.id.rcv_income)
        val incomeListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_INCOME)
        incomeListView.adapter = incomeListAdapter
        val expenseListView: RecyclerView = view.findViewById(R.id.rcv_expense)
        val expenseListAdapter = ReportCListAdapter(UtilCategory.CATEGORY_COLOR_EXPENSE)
        expenseListView.adapter = expenseListAdapter
        _itemStatusViewModel!!.allByCategory.observe(viewLifecycleOwner, { allByCategory ->
            incomeListAdapter.setAllByCategory(allByCategory.filter { it.key.second > BigDecimal(0) })
            expenseListAdapter.setAllByCategory(allByCategory.filter { it.key.second < BigDecimal(0) })
        })

        val btnC: Button = view.findViewById(R.id.btn_c)
        val btnD: Button = view.findViewById(R.id.btn_d)
        val reportC: NestedScrollView = view.findViewById(R.id.fragment_report_c)
        val reportD: NestedScrollView = view.findViewById(R.id.fragment_report_d)
        btnC.setOnClickListener { v ->
            reportC.visibility = VISIBLE
            reportD.visibility = GONE
            MainActivity.medium.currentlyShown.set(Medium.REPORT_C)
            bannerDatePickerBinding.executePendingBindings()
        }
        btnD.setOnClickListener { v ->
            reportC.visibility = GONE
            reportD.visibility = VISIBLE
            MainActivity.medium.currentlyShown.set(Medium.REPORT_D)
            bannerDatePickerBinding.executePendingBindings()
        }

        srlReload = view.findViewById(R.id.srl_reload)
        btnClose = view.findViewById(R.id.btn_exit_search_result)
        srlReload?.setOnRefreshListener {
            Handler().postDelayed({
                srlReload?.isRefreshing = false
                reset()
            }, SWIPE_REFRESH_MILLI_SECOND.toLong())
        }
        btnClose?.setOnClickListener(ButtonClickListener())
//        reset()

        return view
    }

    private fun updateBarChart() {

    }

    private fun updateIncomePieGraph(pieGraph: PieGraph, incomeList: List<BigDecimal>) {
        pieGraph.removeSlices()
        var inPieSlice: PieSlice

        val size = Constants.CATEGORY_EXPENSE_COLORS.size
        for ((i, item) in incomeList.withIndex()) {
            inPieSlice = PieSlice()
            inPieSlice.color =
                    if (i < size) Constants.CATEGORY_INCOME_COLORS[i].toColorInt()
                    else Constants.CATEGORY_INCOME_COLORS[size - 1].toColorInt()
            inPieSlice.value = item.toFloat()
            pieGraph.addSlice(inPieSlice)
        }
    }

    private fun updateExpensePieGraph(pieGraph: PieGraph, expenseList: List<BigDecimal>) {
        pieGraph.removeSlices()
        var exPieSlice: PieSlice

        val size = Constants.CATEGORY_EXPENSE_COLORS.size
        for ((i, item) in expenseList.withIndex()) {
            exPieSlice = PieSlice()
            exPieSlice.color =
                    if (i < size) Constants.CATEGORY_EXPENSE_COLORS[i].toColorInt()
                    else Constants.CATEGORY_EXPENSE_COLORS[size - 1].toColorInt()
            exPieSlice.value = item.toFloat()
            pieGraph.addSlice(exPieSlice)
        }
    }

    /*** onResume will be called just upon viewpager page change  */
    override fun onResume() {
        super.onResume()

//        View view = (View) getView().getRootView().getWindowToken();
//        UtilKeyboard.hideKeyboardFrom(_activity, view);

//        ((InputMethodManager) _activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
//                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
    //    @Override
    //    public void onPause() {
    //        super.onPause();
    //        UtilKeyboard.hideSoftKeyboard(_activity);
    //    }
    /***
     * called when btnNext or btnPrev is clicked
     * builds query with new year and month value
     */
    private fun buildQuery() {
        val y = _calYear.toString()
        val m = convertMtoMM(_calMonth)
        _query = Query(Query.QUERY_TYPE_NEW)
        init(ArrayList())
        setDate("$y-$m-01", "") //todo settings start date of the month
        setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
        setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC)
        setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
        setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC)
        build(_query!!)
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_exit_search_result -> exitSearchResult()
            }
        }
    }

    private fun reset() {
        Log.d(TAG, "reset() called")
        when (_query!!.type) {
            Query.QUERY_TYPE_NEW -> {
//                if (_eventDate == null || "" == _eventDate) {
//                    val cal = Calendar.getInstance()
//                    _calMonth = cal[Calendar.MONTH] + 1
//                    _calYear = cal[Calendar.YEAR]
//                } else {
//                    _calMonth = _eventDate!!.split("-").toTypedArray()[1].toInt()
//                    _calYear = _eventDate!!.split("-").toTypedArray()[0].toInt()
//                }
//                btnDate!!.text = textBtnDate
//                btnNext!!.visibility = View.VISIBLE
//                btnPrev!!.visibility = View.VISIBLE
//                btnClose!!.visibility = View.GONE
//                rootView!!.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(), R.color.colorBackground))
            }
            Query.QUERY_TYPE_SEARCH -> {
//                btnDate!!.text = getString(R.string.search_result)
//                btnNext!!.visibility = View.INVISIBLE
//                btnPrev!!.visibility = View.GONE
//                btnClose!!.visibility = View.VISIBLE
//                rootView!!.setBackgroundColor(
//                        ContextCompat.getColor(requireContext(), R.color.colorBackground_search))
            }
        }
    }

    /* Called from MainActivity */
    fun focusOnSavedItem(query: Query?, eventDate: String?) {
        _query = query
        _eventDate = eventDate
        reset()
//        _ftrDetail = _fragmentManager!!.beginTransaction()
//        _tabFragment2D = TabFragment2D.newInstance(this, query)
//        _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
//        _ftrDetail!!.addToBackStack(null)
//        _ftrDetail!!.commit()
    }

    /* Called from MainActivity */
    fun onSearch(query: Query?, fromDate: String?, toDate: String?) {
        _query = query
        reset()
//        _ftrDetail = _fragmentManager!!.beginTransaction()
//        _tabFragment2D = TabFragment2D.newInstance(this, query)
//        _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
//        _ftrDetail!!.addToBackStack(null)
//        _ftrDetail!!.commit()
    }

    override fun onItemsLoaded(balance: Balance) {
        Log.d(TAG, "onItemsLoaded() called")
//        if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
//            if (_eventDate == null || "" == _eventDate) {
//                _eventDate = getTodaysDate(UtilDate.DATE_FORMAT_DB)
//            }
//            _tabFragment2D!!.focusOnSavedItem(_eventDate!!)
//        }
    }

    fun export() {
//        if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
//            val f = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
//            f!!.export()
//        } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
//            val f = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
//            f!!.export()
//        }
    }

    private fun exitSearchResult(): Boolean {
        val dialogSaveSearch = AlertDialog.Builder(_activity)
        dialogSaveSearch.setIcon(R.mipmap.ic_mikan)
        dialogSaveSearch.setTitle(getString(R.string.returning_to_monthly_report))
        dialogSaveSearch.setMessage(getString(R.string.msg_exit_search))
        dialogSaveSearch.setPositiveButton(R.string.ok) { dialog, which ->
            val todaysDate = getTodaysDate(UtilDate.DATE_FORMAT_DB)
            _query = Query(Query.QUERY_TYPE_NEW)
            init(ArrayList())
            setDate(todaysDate, "")
            setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
            setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC)
            setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
            setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC)
            build(_query!!)
            reset()
//            if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
//                _tabFragment2C = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
//                _tabFragment2C!!.setQuery(_query)
//                _tabFragment2C!!.loadItemsOrderByCategory()
//            } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
//                _tabFragment2D = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
//                _tabFragment2D!!.setQuery(_query)
//                _tabFragment2D!!.loadItemsOrderByDate()
//            }
        }
        dialogSaveSearch.show()
        return true
    }
}