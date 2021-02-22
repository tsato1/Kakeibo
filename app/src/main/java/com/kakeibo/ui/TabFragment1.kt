package com.kakeibo.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.FragmentInputBinding
import com.kakeibo.ui.adapter.CategoryGridAdapter
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.listener.CategoryClickListener
import com.kakeibo.ui.model.Query
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
import com.kakeibo.util.*
import com.kakeibo.util.QueryBuilder.build
import com.kakeibo.util.QueryBuilder.init
import com.kakeibo.util.QueryBuilder.setCGroupBy
import com.kakeibo.util.QueryBuilder.setCOrderBy
import com.kakeibo.util.QueryBuilder.setCsWhere
import com.kakeibo.util.QueryBuilder.setDOrderBy
import com.kakeibo.util.QueryBuilder.setDate
import com.kakeibo.util.UtilDate.convertDateFormat
import com.kakeibo.util.UtilDate.getTodaysDate
import com.kakeibo.util.UtilDate.getTodaysDateWithDay
import java.math.BigDecimal

/**
 * Created by T on 2015/09/14.
 */
class TabFragment1 : Fragment(), CategoryClickListener {
    private var _activity: Activity? = null
    private var _btnDate: Button? = null
    private var _edtAmount: EditText? = null
    private var _edtMemo: EditText? = null

    companion object {
        private val TAG = TabFragment1::class.java.simpleName
        private lateinit var _itemStatusViewModel: ItemStatusViewModel
        private lateinit var _categoryStatusViewModel: CategoryStatusViewModel
        private var _query: Query? = null

        fun newInstance(): TabFragment1 {
            val tabFragment1 = TabFragment1()
            val args = Bundle()
            tabFragment1.arguments = args
            return tabFragment1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _activity = activity

        _itemStatusViewModel =
                ViewModelProviders.of(requireActivity())[ItemStatusViewModel::class.java]
        _categoryStatusViewModel =
                ViewModelProviders.of(requireActivity())[CategoryStatusViewModel::class.java]
        val fragmentBinding: FragmentInputBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_input, container, false)
        fragmentBinding.lifecycleOwner = this
        val view = fragmentBinding.root

//        val bannerDatePickerBinding = fragmentBinding.bannerDatePicker
//        bannerDatePickerBinding.medium = MainActivity.medium

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_grid)
        val categoryGridAdapter = CategoryGridAdapter(this)
        recyclerView.adapter = categoryGridAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, MainActivity.numColumns)
        _categoryStatusViewModel.allDsp.observe(viewLifecycleOwner, {
            categoryGridAdapter.setCategoryStatuses(it)
        })
        _categoryStatusViewModel.allCodes.observe(viewLifecycleOwner, {
            _query = Query(Query.QUERY_TYPE_NEW)
            init(it)
        })

        _btnDate = view.findViewById(R.id.btn_date)
        _edtAmount = view.findViewById(R.id.edt_amount)
        _edtMemo = view.findViewById(R.id.edt_memo)
        _edtAmount?.addTextChangedListener(AmountTextWatcher(_edtAmount!!))

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        _btnDate!!.text = getTodaysDateWithDay(
                SubApp.getDateFormat(R.string.pref_key_date_format),
                resources.getStringArray(R.array.week_name))
        _edtAmount!!.setText("")
        _edtMemo!!.setText("")
    }

    override fun onCategoryClicked(categoryCode: Int) {
        val result = UtilText.checkBeforeSave(_edtAmount!!.text.toString())
        if (!result.first) {
            Toast.makeText(context, requireActivity().getString(result.second), Toast.LENGTH_SHORT).show()
            return
        }

        val eventDate = convertDateFormat(_btnDate!!.text.toString().split(" ")[0], MainActivity.dateFormat, 3)
        val updateDate = getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
        val amount = when (MainActivity.allCategoryStatusMap[categoryCode]!!.color) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                BigDecimal(_edtAmount!!.text.toString())
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                BigDecimal(_edtAmount!!.text.toString()).negate()
            }
            else -> {
                BigDecimal(0)
            }
        }
        val itemStatus = ItemStatus(
                amount,
                UtilCurrency.CURRENCY_NONE,
                categoryCode,
                _edtMemo!!.text.toString(),
                eventDate,
                updateDate
        )

        _itemStatusViewModel.insert(itemStatus)
        Toast.makeText(activity, resources.getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show()
        setDate(eventDate, "")
        setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
        setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC)
        setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
        setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC)
        build(_query!!)
        (_activity as MainActivity).onItemSaved(_query!!, eventDate)
        _btnDate!!.text = getTodaysDateWithDay(MainActivity.dateFormat, MainActivity.weekNames)
    }
}