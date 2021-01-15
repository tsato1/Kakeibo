package com.kakeibo.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.ui.items.Balance
import com.kakeibo.util.QueryBuilder
import com.kakeibo.util.QueryBuilder.build
import com.kakeibo.util.QueryBuilder.init
import com.kakeibo.util.QueryBuilder.setCGroupBy
import com.kakeibo.util.QueryBuilder.setCOrderBy
import com.kakeibo.util.QueryBuilder.setCsWhere
import com.kakeibo.util.QueryBuilder.setDOrderBy
import com.kakeibo.util.QueryBuilder.setDate
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.convertMtoMM
import com.kakeibo.util.UtilDate.getTodaysDate
import java.util.*
import javax.annotation.Nonnull

/**
 * Created by T on 2015/09/14.
 */
class TabFragment2 : Fragment(), ItemLoadListener {
    private var _activity: Activity? = null
    private var rootView: CoordinatorLayout? = null
    private var srlReload: SwipeRefreshLayout? = null
    private var btnPrev: ImageButton? = null
    private var btnNext: ImageButton? = null
    private var btnClose: ImageButton? = null
    private var btnDate: Button? = null
    private var txvIncome: TextView? = null
    private var txvExpense: TextView? = null
    private var txvBalance: TextView? = null
    private var _fragmentManager: FragmentManager? = null
    private var _ftrDetail: FragmentTransaction? = null
    private var _tabFragment2C: TabFragment2C? = null
    private var _tabFragment2D: TabFragment2D? = null
    private var _balance: Balance? = null

    override fun onAttach(@Nonnull context: Context) {
        super.onAttach(context)
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
        /*** preparing fragment in detail view  */
        _fragmentManager = childFragmentManager
        _ftrDetail = _fragmentManager!!.beginTransaction()
        _tabFragment2D = TabFragment2D.newInstance(this, _query)
        _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
        _ftrDetail!!.addToBackStack(null)
        _ftrDetail!!.commit()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _activity = activity
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        rootView = view.findViewById(R.id.col_root_fragment2)
        srlReload = view.findViewById(R.id.srl_reload)
        btnPrev = view.findViewById(R.id.btn_prev)
        btnDate = view.findViewById(R.id.btn_date)
        btnNext = view.findViewById(R.id.btn_next)
        btnClose = view.findViewById(R.id.btn_exit_search_result)
        txvIncome = view.findViewById(R.id.txv_income)
        txvExpense = view.findViewById(R.id.txv_expense)
        txvBalance = view.findViewById(R.id.txv_balance)
        srlReload?.setOnRefreshListener(OnRefreshListener {
            Handler().postDelayed({
                srlReload?.setRefreshing(false)
                reset()
            }, SWIPE_REFRESH_MILLI_SECOND.toLong())
        })
        btnPrev?.setOnClickListener(ButtonClickListener())
        btnDate?.setOnClickListener(ButtonClickListener())
        btnNext?.setOnClickListener(ButtonClickListener())
        btnClose?.setOnClickListener(ButtonClickListener())
        btnDate?.setOnLongClickListener(ButtonLongClickListener())
        reset()
        return view
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

    internal inner class ButtonLongClickListener : OnLongClickListener {
        override fun onLongClick(view: View): Boolean {
            return true
        }
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_date -> toggleViews()
                R.id.btn_prev -> {
                    _calMonth--
                    if (_calMonth <= 0) {
                        _calMonth = 12
                        _calYear--
                        if (_calYear <= 0) {
                            _calYear = Calendar.getInstance()[Calendar.YEAR]
                        }
                    }
                    btnDate!!.text = textBtnDate
                    buildQuery()
                    if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
                        _tabFragment2C = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
                        _tabFragment2C!!.setQuery(_query)
                        _tabFragment2C!!.loadItemsOrderByCategory()
                    } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
                        _tabFragment2D = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
                        _tabFragment2D!!.setQuery(_query)
                        _tabFragment2D!!.loadItemsOrderByDate()
                    }
                }
                R.id.btn_next -> {
                    _calMonth++
                    if (_calMonth > 12) {
                        _calMonth = 1
                        _calYear++
                    }
                    btnDate!!.text = textBtnDate
                    buildQuery()
                    if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
                        _tabFragment2C = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
                        _tabFragment2C!!.setQuery(_query)
                        _tabFragment2C!!.loadItemsOrderByCategory()
                    } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
                        _tabFragment2D = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
                        _tabFragment2D!!.setQuery(_query)
                        _tabFragment2D!!.loadItemsOrderByDate()
                    }
                }
                R.id.btn_exit_search_result -> exitSearchResult()
            }
        }
    }

    private val textBtnDate: String
        get() {
            val year = _calYear
            val month = _calMonth
            val str: String
            str = when (_dateFormat) {
                1, 2 -> convertMtoMM(month) + "/" + year
                else -> year.toString() + "/" + convertMtoMM(month)
            }
            return str
        }

    private fun makeBalanceTable() {
        txvIncome!!.text = _balance!!.income.toString()
        txvExpense!!.text = _balance!!.expense.toString()
        if (_balance!!.inMinusOut() < 0) {
            txvBalance!!.setTextColor(ContextCompat.getColor(_activity!!, R.color.colorRed))
            txvBalance!!.text = _balance!!.balance.toString()
        } else if (_balance!!.inMinusOut() > 0) {
            txvBalance!!.setTextColor(ContextCompat.getColor(_activity!!, R.color.colorBlue))
            val str = "+" + _balance!!.balance
            txvBalance!!.text = str
        } else {
            txvBalance!!.setTextColor(ContextCompat.getColor(_activity!!, R.color.colorBlack))
            txvBalance!!.text = _balance!!.balance.toString()
        }
    }

    private fun reset() {
        Log.d(TAG, "reset() called")
        when (_query!!.type) {
            Query.QUERY_TYPE_NEW -> {
                if (_eventDate == null || "" == _eventDate) {
                    val cal = Calendar.getInstance()
                    _calMonth = cal[Calendar.MONTH] + 1
                    _calYear = cal[Calendar.YEAR]
                } else {
                    _calMonth = _eventDate!!.split("-").toTypedArray()[1].toInt()
                    _calYear = _eventDate!!.split("-").toTypedArray()[0].toInt()
                }
                btnDate!!.text = textBtnDate
                btnNext!!.visibility = View.VISIBLE
                btnPrev!!.visibility = View.VISIBLE
                btnClose!!.visibility = View.GONE
                rootView!!.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.colorBackground))
            }
            Query.QUERY_TYPE_SEARCH -> {
                btnDate!!.text = getString(R.string.search_result)
                btnNext!!.visibility = View.INVISIBLE
                btnPrev!!.visibility = View.GONE
                btnClose!!.visibility = View.VISIBLE
                rootView!!.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.colorBackground_search))
            }
        }
    }

    /* Called from MainActivity */
    fun focusOnSavedItem(query: Query?, eventDate: String?) {
        _query = query
        _eventDate = eventDate
        reset()
        _ftrDetail = _fragmentManager!!.beginTransaction()
        _tabFragment2D = TabFragment2D.newInstance(this, query)
        _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
        _ftrDetail!!.addToBackStack(null)
        _ftrDetail!!.commit()
    }

    /* Called from MainActivity */
    fun onSearch(query: Query?, fromDate: String?, toDate: String?) {
        _query = query
        reset()
        _ftrDetail = _fragmentManager!!.beginTransaction()
        _tabFragment2D = TabFragment2D.newInstance(this, query)
        _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
        _ftrDetail!!.addToBackStack(null)
        _ftrDetail!!.commit()
    }

    override fun onItemsLoaded(balance: Balance) {
        Log.d(TAG, "onItemsLoaded() called")
        _balance = balance
        makeBalanceTable()
        if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
            if (_eventDate == null || "" == _eventDate) {
                _eventDate = getTodaysDate(UtilDate.DATE_FORMAT_DB)
            }
            _tabFragment2D!!.focusOnSavedItem(_eventDate!!)
        }
    }

    private fun toggleViews() {
        if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
            Log.d(TAG, "2c detail fragment is visible")
            _ftrDetail = _fragmentManager!!.beginTransaction()
            _tabFragment2D = TabFragment2D.newInstance(this@TabFragment2, _query)
            _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2D!!)
            _ftrDetail!!.addToBackStack(null)
            _ftrDetail!!.commit()
        } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
            Log.d(TAG, "2d detail fragment is visible")
            _ftrDetail = _fragmentManager!!.beginTransaction()
            _tabFragment2C = TabFragment2C.newInstance(this@TabFragment2, _query)
            _ftrDetail!!.replace(R.id.frl_tab2_container, _tabFragment2C!!)
            _ftrDetail!!.addToBackStack(null)
            _ftrDetail!!.commit()
        }
    }

    fun export() {
        if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
            val f = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
            f!!.export()
        } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
            val f = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
            f!!.export()
        }
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
            if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2C) {
                _tabFragment2C = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2C?
                _tabFragment2C!!.setQuery(_query)
                _tabFragment2C!!.loadItemsOrderByCategory()
            } else if (_fragmentManager!!.findFragmentById(R.id.frl_tab2_container) is TabFragment2D) {
                _tabFragment2D = _fragmentManager!!.findFragmentById(R.id.frl_tab2_container) as TabFragment2D?
                _tabFragment2D!!.setQuery(_query)
                _tabFragment2D!!.loadItemsOrderByDate()
            }
        }
        dialogSaveSearch.show()
        return true
    }

    companion object {
        private val TAG = TabFragment2::class.java.simpleName
        private const val SWIPE_REFRESH_MILLI_SECOND = 400
        var REPORT_BY_DATE = 0
        var REPORT_BY_CATEGORY = 1
        var REPORT_BY_AMOUNT = 2 //todo
        private var _dateFormat = 0
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
}