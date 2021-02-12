package com.kakeibo.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.kakeibo.ui.adapter.CategoryDspStatusViewModel
import com.kakeibo.ui.adapter.CategoryGridAdapter
import com.kakeibo.ui.adapter.CategoryStatusViewModel
import com.kakeibo.ui.listener.CategoryClickListener
import com.kakeibo.util.QueryBuilder
import com.kakeibo.util.QueryBuilder.build
import com.kakeibo.util.QueryBuilder.init
import com.kakeibo.util.QueryBuilder.setCGroupBy
import com.kakeibo.util.QueryBuilder.setCOrderBy
import com.kakeibo.util.QueryBuilder.setCsWhere
import com.kakeibo.util.QueryBuilder.setDOrderBy
import com.kakeibo.util.QueryBuilder.setDate
import com.kakeibo.util.UtilCurrency
import com.kakeibo.util.UtilCurrency.checkAmount
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.convertDateFormat
import com.kakeibo.util.UtilDate.getTodaysDate
import com.kakeibo.util.UtilDate.getTodaysDateWithDay
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by T on 2015/09/14.
 */
class TabFragment1 : Fragment(), CategoryClickListener {
    private var _activity: Activity? = null
    private var _btnDate: Button? = null
    private var _edtAmount: EditText? = null
    private var _edtMemo: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _activity = activity
        _weekNames = resources.getStringArray(R.array.week_name)
        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        _numColumns = SubApp.getNumColumns(R.string.pref_key_num_columns)

        val billingViewModel =
                ViewModelProviders.of(requireActivity())[BillingViewModel::class.java]
        val subscriptionViewModel =
                ViewModelProviders.of(requireActivity())[SubscriptionStatusViewModel::class.java]
        _itemStatusViewModel =
                ViewModelProviders.of(requireActivity())[ItemStatusViewModel::class.java]
        val categoryStatusViewModel =
                ViewModelProviders.of(requireActivity())[CategoryStatusViewModel::class.java]
        val categoryDspStatusViewModel =
                ViewModelProviders.of(requireActivity())[CategoryDspStatusViewModel::class.java]

        val fragmentBinding: FragmentInputBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_input, container, false)

        fragmentBinding.lifecycleOwner = this
        fragmentBinding.billingViewModel = billingViewModel
        fragmentBinding.subscriptionViewModel = subscriptionViewModel
        fragmentBinding.itemStatusViewModel = _itemStatusViewModel
        fragmentBinding.categoryStatusViewModel = categoryStatusViewModel
        fragmentBinding.categoryDspStatusViewModel = categoryDspStatusViewModel

        val view = fragmentBinding.root

        categoryStatusViewModel.allCodes.observe(viewLifecycleOwner, {
            _query = Query(Query.QUERY_TYPE_NEW)
            init(it)
        })

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_input_categories)

        val categoryGridAdapter = CategoryGridAdapter(this)

        categoryStatusViewModel.categoriesForDisplay.observe(viewLifecycleOwner, {
            categoryGridAdapter.setCategoryStatuses(it)
        })

        recyclerView.adapter = categoryGridAdapter
        recyclerView.layoutManager = GridLayoutManager(_activity, _numColumns)

        val btnPrev = view.findViewById<ImageButton>(R.id.btn_prev)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_next)
        _btnDate = view.findViewById(R.id.btn_date)
        _edtAmount = view.findViewById(R.id.edt_amount)
        _edtMemo = view.findViewById(R.id.edt_memo)
        btnPrev.setOnClickListener(ButtonClickListener())
        btnNext.setOnClickListener(ButtonClickListener())
        _btnDate?.setOnClickListener(ButtonClickListener())
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

    override fun onPause() {
        super.onPause()
        //        _activity.getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//        );
//        ((InputMethodManager) _activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
//                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private fun checkBeforeSave(): Boolean {
        if ("" == _edtAmount!!.text.toString()) {
            Toast.makeText(activity, R.string.err_please_enter_amount, Toast.LENGTH_SHORT).show()
            return false
        }
        if ("0" == _edtAmount!!.text.toString() || "0.0" == _edtAmount!!.text.toString() || "0.00" == _edtAmount!!.text.toString() || "0.000" == _edtAmount!!.text.toString()) {
            Toast.makeText(activity, R.string.err_amount_cannot_be_0, Toast.LENGTH_SHORT).show()
            return false
        }
        if (!checkAmount(_edtAmount!!.text.toString())) {
            Toast.makeText(activity, R.string.err_amount_invalid, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onCategoryClicked(categoryCode: Int) {
        if (!checkBeforeSave()) return

        val eventDate = convertDateFormat(_btnDate!!.text.toString().split(" ")[0], _dateFormat, 3)
        val updateDate = getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
        val amount = _edtAmount!!.text.toString()
        val itemStatus = ItemStatus(
                BigDecimal(amount),
                UtilCurrency.CURRENCY_NONE,
                categoryCode,
                _edtMemo!!.text.toString(),
                eventDate,
                updateDate
        )

        _itemStatusViewModel!!.insert(itemStatus)
        Toast.makeText(activity, resources.getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show()
        setDate(eventDate, "")
        setCGroupBy(ItemDBAdapter.COL_CATEGORY_CODE)
        setCOrderBy(QueryBuilder.SUM_AMOUNT, QueryBuilder.DESC)
        setCsWhere(ItemDBAdapter.COL_CATEGORY_CODE)
        setDOrderBy(ItemDBAdapter.COL_EVENT_DATE, QueryBuilder.ASC)
        build(_query!!)
        (_activity as MainActivity).onItemSaved(_query!!, eventDate)
        _btnDate!!.text = getTodaysDateWithDay(_dateFormat, _weekNames!!)
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            val sourceDate = _btnDate!!.text.toString().substring(0, 10)
            Log.d("sourceDate=", sourceDate)
            val format = SimpleDateFormat(
                    UtilDate.DATE_FORMATS[_dateFormat],
                    Locale.getDefault())
            var date: Date? = null
            val cal = Calendar.getInstance()
            when (view.id) {
                R.id.btn_prev -> {
                    try {
                        date = format.parse(sourceDate)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    cal.time = date!!
                    cal.add(Calendar.DATE, -1)
                    date = cal.time
                    val str = (SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                            Locale.getDefault()).format(date!!)
                            + " [" + _weekNames!![cal[Calendar.DAY_OF_WEEK] - 1] + "]")
                    _btnDate!!.text = str
                }
                R.id.btn_date -> showYMDPickerDialog()
                R.id.btn_next -> {
                    try {
                        date = format.parse(sourceDate)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    cal.time = date!!
                    cal.add(Calendar.DATE, 1)
                    date = cal.time
                    val str = (SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                            Locale.getDefault()).format(date!!)
                            + " [" + _weekNames!![cal[Calendar.DAY_OF_WEEK] - 1] + "]")
                    _btnDate?.setText(str)
                }
            }
        }
    }

    private fun showYMDPickerDialog() {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH] + 1
        val day = cal[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(_activity!!, { picker, year, month, day ->
            val cal = GregorianCalendar(year, month, day)
            val date = cal.time
            val str = (SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                    Locale.getDefault()).format(date)
                    + " [" + _weekNames!![cal[Calendar.DAY_OF_WEEK] - 1] + "]")
            _btnDate!!.text = str
        }, year, month - 1, day)
        dialog.show()
    }

    companion object {
        private val TAG = TabFragment1::class.java.simpleName
        private var _itemStatusViewModel: ItemStatusViewModel? = null
        private var _weekNames: Array<String>? = null
        private var _dateFormat = 0
        private var _numColumns = 0
        private var _query: Query? = null

        fun newInstance(): TabFragment1 {
            val tabFragment1 = TabFragment1()
            val args = Bundle()
            tabFragment1.arguments = args
            return tabFragment1
        }
    }
}