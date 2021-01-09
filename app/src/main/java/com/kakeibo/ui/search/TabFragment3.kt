//package com.kakeibo.ui.search
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.coordinatorlayout.widget.CoordinatorLayout
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.DefaultItemAnimator
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.sqlite.db.SimpleSQLiteQuery
//import com.google.android.material.snackbar.Snackbar
//import com.kakeibo.R
//import com.kakeibo.SubApp
//import com.kakeibo.data.ItemStatus
//import com.kakeibo.databinding.FragmentSearchBinding
//import com.kakeibo.db.ItemDBAdapter
//import com.kakeibo.ui.*
//import com.kakeibo.ui.categories.CategoryDspStatusViewModel
//import com.kakeibo.ui.categories.CategoryStatusViewModel
//import com.kakeibo.ui.search.SearchRecyclerViewAdapter.*
//import com.kakeibo.util.UtilCurrency
//import com.kakeibo.util.UtilDate
//import com.kakeibo.util.UtilKeyboard
//import com.kakeibo.util.UtilQuery
//import kotlinx.coroutines.*
//import java.math.BigDecimal
//import java.util.*
//import kotlin.coroutines.CoroutineContext
//
//class TabFragment3 : Fragment(), RecyclerItemTouchHelperListener, CoroutineScope {
//    private var _activity: Activity? = null
//
//    // Kotlin Coroutine
//    private var job: Job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job
//
//    private var _viewRoot: CoordinatorLayout? = null
//    private var _txvSearchInstruction: TextView? = null
//    private var _rcvSearchCriteria: RecyclerView? = null
//    private var _adpRecyclerView: SearchRecyclerViewAdapter? = null
//    private var _query: Query? = null
//    private var _lstCards // for cards displayed
//            : ArrayList<Card>? = null
//    private var _lstChoices // for choices shown in dialog upon tapping fab
//            : ArrayList<String>? = null
//    private var _fromDate: String? = null
//    private var _toDate: String? = null
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        _activity = activity
//        _searchCriteria = resources.getStringArray(R.array.search_criteria)
//        _lstChoices = ArrayList(Arrays.asList(*_searchCriteria))
//        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
//        val billingViewModel = ViewModelProviders.of(requireActivity())[BillingViewModel::class.java]
//        val subscriptionViewModel = ViewModelProviders.of(requireActivity())[SubscriptionStatusViewModel::class.java]
//        val itemStatusViewModel = ViewModelProviders.of(requireActivity())[ItemStatusViewModel::class.java]
//        val categoryStatusViewModel = ViewModelProviders.of(requireActivity())[CategoryStatusViewModel::class.java]
//        val categoryDspStatusViewModel = ViewModelProviders.of(requireActivity())[CategoryDspStatusViewModel::class.java]
//        val fragmentBinding: FragmentSearchBinding = DataBindingUtil.inflate(
//                inflater, R.layout.fragment_search, container, false)
//        fragmentBinding.lifecycleOwner = this
//        fragmentBinding.billingViewModel = billingViewModel
//        fragmentBinding.subscriptionViewModel = subscriptionViewModel
//        fragmentBinding.itemStatusViewModel = itemStatusViewModel
//        fragmentBinding.categoryStatusViewModel = categoryStatusViewModel
//        fragmentBinding.categoryDspStatusViewModel = categoryDspStatusViewModel
//        val view = fragmentBinding.root
//        categoryStatusViewModel.categoryCodes.observe(viewLifecycleOwner, Observer { categoryCodes ->
//            _query = Query(Query.QUERY_TYPE_SEARCH)
//            UtilQuery.init(categoryCodes)
//        })
//        _viewRoot = view.findViewById(R.id.col_root_fragment3)
//        _txvSearchInstruction = view.findViewById(R.id.txv_inst_search)
//        _rcvSearchCriteria = view.findViewById(R.id.rcv_search_criteria)
//        _lstCards = ArrayList()
//        _adpRecyclerView = SearchRecyclerViewAdapter(context, _lstCards)
//        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
//        _rcvSearchCriteria!!.setLayoutManager(layoutManager)
//        _rcvSearchCriteria!!.setItemAnimator(DefaultItemAnimator())
//        _rcvSearchCriteria!!.setAdapter(_adpRecyclerView)
//        val ithCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
//        ItemTouchHelper(ithCallback).attachToRecyclerView(_rcvSearchCriteria)
//        return view
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume() called")
//        UtilKeyboard.hideSoftKeyboard(_activity)
//        if (_lstCards!!.size == 0) {
//            _txvSearchInstruction!!.visibility = View.VISIBLE
//        } else {
//            _txvSearchInstruction!!.visibility = View.INVISIBLE
//        }
//        val indexAmountRangeCard = _lstCards!!.indexOf(Card(Card.TYPE_AMOUNT_RANGE, 0))
//        if (indexAmountRangeCard > -1) {
//            val viewHolder = _rcvSearchCriteria!!.findViewHolderForAdapterPosition(indexAmountRangeCard)
//            if (viewHolder is ViewHolderAmountRange) {
//                val viewHolderAmountRange = viewHolder
//                viewHolderAmountRange.edtMin.setText("")
//                viewHolderAmountRange.edtMax.setText("")
//            }
//        }
//        _adpRecyclerView!!.notifyDataSetChanged()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        UtilKeyboard.hideSoftKeyboard(_activity)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        job.cancel()
//    }
//
//    private fun checkBeforeSearch(): Boolean {
//        if (_lstCards!!.size == 0) {
//            Toast.makeText(_activity,
//                    resources.getString(R.string.err_no_search_criteria_found),
//                    Toast.LENGTH_SHORT).show()
//            return false
//        }
//        val indexDateRangeCard = _lstCards!!.indexOf(Card(Card.TYPE_DATE_RANGE, 0))
//        if (indexDateRangeCard > -1) {
//            val viewHolder = _rcvSearchCriteria!!.findViewHolderForAdapterPosition(indexDateRangeCard)
//            if (viewHolder is ViewHolderDateRange) {
//                val viewHolderDateRange = viewHolder
//                _fromDate = viewHolderDateRange.btnFrom.text.toString()
//                _toDate = viewHolderDateRange.btnTo.text.toString()
//                if ("" == _fromDate) {
//                    Toast.makeText(activity, resources.getString(R.string.err_please_choose_from_date), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                if ("" == _toDate) {
//                    Toast.makeText(activity, resources.getString(R.string.err_please_choose_to_date), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                if (UtilDate.compareDate(_fromDate, _toDate, _dateFormat) == -1) {
//                    Toast.makeText(activity, resources.getString(R.string.err_from_date_older), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                UtilQuery.setDate(
//                        UtilDate.convertDateFormat(_fromDate, _dateFormat, 3),
//                        UtilDate.convertDateFormat(_toDate, _dateFormat, 3))
//            }
//        }
//        val indexAmountRangeCard = _lstCards!!.indexOf(Card(Card.TYPE_AMOUNT_RANGE, 0))
//        if (indexAmountRangeCard > -1) {
//            val viewHolder = _rcvSearchCriteria!!.findViewHolderForAdapterPosition(indexAmountRangeCard)
//            if (viewHolder is ViewHolderAmountRange) {
//                val viewHolderAmountRange = viewHolder
//                val min = viewHolderAmountRange.edtMin.text.toString()
//                val max = viewHolderAmountRange.edtMax.text.toString()
//                if ("" == min) {
//                    Toast.makeText(activity, resources.getString(R.string.err_please_enter_min_amount), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                if ("" == max) {
//                    Toast.makeText(activity, resources.getString(R.string.err_please_enter_max_amount), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                if (
//                /***"0".equals(min) || */
//                        "0" == max || "0.0" == max || "0.00" == max || "0.000" == max) {
//                    Toast.makeText(activity, resources.getString(R.string.err_max_amount_cannot_be_0), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                val bigMin = BigDecimal(min)
//                val bigMax = BigDecimal(max)
//                if (bigMin.compareTo(bigMax) > 0) {
//                    Toast.makeText(activity, resources.getString(R.string.err_min_amount_greater), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                /*** using 3 (multiply by 1000) to compare with what's stored in db  */
//                UtilQuery.setAmount(UtilCurrency.getLongAmountFromBigDecimal(bigMin, 3),
//                        UtilCurrency.getLongAmountFromBigDecimal(bigMax, 3))
//            }
//        }
//        val indexCategoryCard = _lstCards!!.indexOf(Card(Card.TYPE_CATEGORY, 0))
//        if (indexCategoryCard > -1) {
//            val viewHolder = _rcvSearchCriteria!!.findViewHolderForAdapterPosition(indexCategoryCard)
//            if (viewHolder is ViewHolderCategory) {
//                val viewHolderCategory = viewHolder
//                val category = viewHolderCategory.btnCategory.text.toString()
//                val categoryCode = viewHolderCategory.getSelectedCategoryCode()
//                if ("" == category) {
//                    Toast.makeText(activity, resources.getString(R.string.err_please_select_category), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                UtilQuery.setCategoryCode(categoryCode.toString())
//            }
//        }
//        val indexMemoCard = _lstCards!!.indexOf(Card(Card.TYPE_MEMO, 0))
//        if (indexMemoCard > -1) {
//            val viewHolder = _rcvSearchCriteria!!.findViewHolderForAdapterPosition(indexMemoCard)
//            if (viewHolder is ViewHolderMemo) {
//                val memo = viewHolder.edtMemo.text.toString()
//                if ("" == memo) {
//                    Toast.makeText(activity, resources.getString(R.string.err_memo_empty), Toast.LENGTH_SHORT).show()
//                    return false
//                }
//                UtilQuery.setMemo(memo)
//            }
//        }
//        return true
//    }
//
//    override fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
//        var name = ""
//        if (viewHolder is ViewHolderDateRange) {
//            name = resources.getString(R.string.date_range)
//            _lstChoices!!.add(0, _searchCriteria[0])
//        } else if (viewHolder is ViewHolderAmountRange) {
//            name = resources.getString(R.string.amount_range)
//            if (_lstChoices!!.size == 0) {
//                _lstChoices!!.add(_searchCriteria[1])
//            } else {
//                _lstChoices!!.add(1, _searchCriteria[1])
//            }
//        } else if (viewHolder is ViewHolderCategory) {
//            name = resources.getString(R.string.category)
//            if (_lstChoices!!.size <= 1) {
//                _lstChoices!!.add(_searchCriteria[2])
//            } else {
//                _lstChoices!!.add(2, _searchCriteria[2])
//            }
//        } else if (viewHolder is ViewHolderMemo) {
//            name = resources.getString(R.string.memo)
//            if (_lstChoices!!.size <= 2) {
//                _lstChoices!!.add(_searchCriteria[3])
//            } else {
//                _lstChoices!!.add(3, _searchCriteria[3])
//            }
//        }
//        val cardItem = _lstCards!![viewHolder.adapterPosition]
//        val deleteIndex = viewHolder.adapterPosition
//        _adpRecyclerView!!.removeItem(deleteIndex)
//        if (_lstCards!!.size == 0) {
//            _txvSearchInstruction!!.visibility = View.VISIBLE
//        }
//        val snackbar = Snackbar.make(_viewRoot!!, name + resources.getString(R.string.msg_card_is_removed), Snackbar.LENGTH_LONG)
//        snackbar.setAction(getString(R.string.undo)) {
//            _adpRecyclerView!!.restoreItem(cardItem, deleteIndex)
//            _txvSearchInstruction!!.visibility = View.INVISIBLE
//            for (i in _lstChoices!!.indices) {
//                if (_lstChoices!![i] == _searchCriteria[cardItem.type]) _lstChoices!!.removeAt(i)
//            }
//        }.setActionTextColor(resources.getColor(R.color.colorPrimary))
//        snackbar.show()
//    }
//
//    fun addCriteria() {
//        val arrToDisplay = _lstChoices!!.toTypedArray()
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(resources.getString(R.string.add_search_criterion))
//        builder.setIcon(R.mipmap.ic_mikan)
//        builder.setItems(arrToDisplay) { dialogInterface, which ->
//            val str = _lstChoices!!.removeAt(which)
//            var selected = 0
//            for (i in _searchCriteria.indices) {
//                if (_searchCriteria[i] == str) selected = i
//            }
//            val card = Card(selected, 0)
//            _lstCards!!.add(card)
//            _adpRecyclerView!!.notifyDataSetChanged()
//            _txvSearchInstruction!!.visibility = View.INVISIBLE
//        }
//        builder.setNegativeButton(R.string.cancel) { dialog, which -> }
//        builder.show()
//    }
//
//    fun onResult(result: List<ItemStatus>) {
//        if (result.isEmpty()) {
//            Toast.makeText(_activity, getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show()
//        }
//        (_activity as MainActivity?)!!.onSearch(_query, _fromDate, _toDate)
//    }
//
//    fun doSearch() {
//        if (checkBeforeSearch()) {
//            UtilQuery.setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
//            UtilQuery.setCOrderBy(UtilQuery.SUM_AMOUNT, UtilQuery.DESC)
//            UtilQuery.setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
//            UtilQuery.setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, UtilQuery.ASC)
//            UtilQuery.build(_query)
//
////            val result = (_activity!!.application as SubApp)
////                    .localDataSource.queryItems(simpleSQLiteQuery, ArrayList<ItemStatus>())
//            GlobalScope.launch {
//                val simpleSQLiteQuery = SimpleSQLiteQuery(_query!!.queryD, arrayOf())
//                val result = (_activity!!.application as SubApp)
//                        .localDataSource.queryItems(simpleSQLiteQuery)
//                onResult(result)
//            }
//
////            ItemDBAdapter itemDbAdapter = new ItemDBAdapter();
////            itemDbAdapter.open();
////            Cursor c = itemDbAdapter.getCountItemsByRawQuery(query.getQueryD());
////
////            if (c.moveToNext()) {
////                /*** if the query returns empty set, toast message ***/
////                if (c.getInt(0)<=0) {
////                    Toast.makeText(_activity, getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show();
////                }
////                /*** if the query returns non-empty set, proceed ***/
////                else {
////                    ((MainActivity) _activity).onSearch(query, _fromDate, _toDate);
////                }
////            } else {
////                /*** if the query returns empty set, toast message ***/
////                Toast.makeText(_activity, getString(R.string.msg_no_result_found), Toast.LENGTH_SHORT).show();
////            }
////
////            itemDbAdapter.close();
//        }
//    }
//
//    companion object {
//        private val TAG = TabFragment3::class.java.simpleName
//        private lateinit var _searchCriteria: Array<String>
//        private var _dateFormat = 0
//
//        @JvmStatic fun newInstance(): TabFragment3 {
//            val tabFragment3 = TabFragment3()
//            val args = Bundle()
//            tabFragment3.arguments = args
//            return tabFragment3;
//        }
//    }
//}