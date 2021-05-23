package com.kakeibo.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.Category
import com.kakeibo.data.Item
import com.kakeibo.databinding.FragmentInputBinding
import com.kakeibo.ui.MainActivity.Companion.dateFormat
import com.kakeibo.ui.MainActivity.Companion.weekNames
import com.kakeibo.ui.viewmodel.CategoryViewModel
import com.kakeibo.ui.listener.CategoryClickListener
import com.kakeibo.ui.settings.category.replace.GridItem
import com.kakeibo.ui.adapter.view.RecyclerViewAdapter
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.viewmodel.ItemViewModel
import com.kakeibo.util.*
import com.kakeibo.util.UtilDate.getTodaysDate
import com.kakeibo.util.UtilDate.getTodaysDateWithDay
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by T on 2015/09/14.
 */
class InputFragment : Fragment(), CategoryClickListener {
    private lateinit var _btnDate: Button
    private lateinit var _edtAmount: EditText
    private lateinit var _edtMemo: EditText

    private val _itemViewModel: ItemViewModel by activityViewModels()
    private val _categoryViewModel: CategoryViewModel by activityViewModels()

    private lateinit var _cal: Calendar

    companion object {
        private val TAG = InputFragment::class.java.simpleName
        private const val SWIPE_REFRESH_MILLI_SECOND = 200

        fun newInstance(): InputFragment {
            val tabFragment1 = InputFragment()
            val args = Bundle()
            tabFragment1.arguments = args
            return tabFragment1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentInputBinding.inflate(inflater, container, false)
        val view = binding.root

        val list = ArrayList<GridItem>()

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_grid)
        val recyclerViewAdapter = RecyclerViewAdapter(list, this)
        recyclerView.layoutManager = GridLayoutManager(activity, MainActivity.numColumns)
        recyclerView.adapter = recyclerViewAdapter
        _categoryViewModel.dsp.observe(viewLifecycleOwner, {
            list.clear()
            it.forEach { p -> list.add(GridItem.ChildItem(p.id, p)) }
            recyclerViewAdapter.notifyDataSetChanged()
        })

        _btnDate = view.findViewById(R.id.btn_date)
        _edtAmount = view.findViewById(R.id.edt_amount)
        _edtMemo = view.findViewById(R.id.edt_memo)
        _edtAmount.addTextChangedListener(AmountTextWatcher(_edtAmount))

        val srlReload = view.findViewById<SwipeRefreshLayout>(R.id.srl_reload)
        srlReload.setOnRefreshListener { //todo
            Handler().postDelayed({
                reset()
                srlReload.isRefreshing = false
            }, SWIPE_REFRESH_MILLI_SECOND.toLong())
        }



        _cal = Calendar.getInstance()

        _btnDate = view.findViewById(R.id.btn_date)
        val btnNext: ImageButton = view.findViewById(R.id.btn_next)
        val btnPrev: ImageButton = view.findViewById(R.id.btn_prev)
        btnNext.setOnClickListener(ButtonClickListener())
        btnPrev.setOnClickListener(ButtonClickListener())

        _btnDate.text = getTodaysDateWithDay(dateFormat, weekNames)
        _btnDate.setOnClickListener {
            val year = _cal[Calendar.YEAR]
            val month = _cal[Calendar.MONTH] + 1
            val day = _cal[Calendar.DAY_OF_MONTH]
            val dialog = DatePickerDialog(requireContext(), { _, y, m, d ->
                val gCal = GregorianCalendar(y, m, d)
                val str = SimpleDateFormat(
                    UtilDate.DATE_FORMATS[dateFormat],
                    Locale.getDefault()
                ).format(gCal.time) + " [" + weekNames[_cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                _btnDate.text = str
            }, year, month - 1, day)
            dialog.show()
        }
        btnNext.visibility = View.VISIBLE
        btnPrev.visibility = View.VISIBLE






        return view
    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume() called")
//    }

    private fun reset() {
        _btnDate.text = getTodaysDateWithDay(
            SubApp.getDateFormat(R.string.pref_key_date_format),
            resources.getStringArray(R.array.week_name))
        _edtAmount.setText("")
        _edtMemo.setText("")
        _cal = Calendar.getInstance()
    }

    override fun onCategoryClicked(view: View, category: Category) {
        val result = UtilText.checkBeforeSave(_edtAmount.text.toString())
        if (!result.first) {
            Toast.makeText(context, getString(result.second), Toast.LENGTH_SHORT).show()
            return
        }

        val eventDate = UtilDate.getDBDate(_btnDate.text.toString().split(" ")[0], dateFormat)
        val updateDate = getTodaysDate(UtilDate.DATE_FORMAT_DB_KMS)
        val amount = when (category.color) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                BigDecimal(_edtAmount.text.toString().replace(',','.'))
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                BigDecimal(_edtAmount.text.toString().replace(',','.')).negate()
            }
            else -> {
                BigDecimal(0)
            }
        }
        val itemStatus = Item(
                amount,
                UtilCurrency.CURRENCY_NONE,
                category.code,
                _edtMemo.text.toString(),
                eventDate,
                updateDate
        )

        _itemViewModel.insert(itemStatus)
        Toast.makeText(activity, resources.getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show()

        (activity as MainActivity).onItemSaved(eventDate)
        hideKeyboard()
        reset()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }




    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            when (v.id) {
                R.id.btn_next -> {
                    try {
                        _cal.add(Calendar.DATE, 1)
                        val date = _cal.time
                        val out = SimpleDateFormat(
                            UtilDate.DATE_FORMATS[dateFormat],
                            Locale.getDefault()
                        ).format(date) + " [" + weekNames[_cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                        _btnDate.text = out
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
                R.id.btn_prev -> {
                    try {
                        _cal.add(Calendar.DATE, -1)
                        val date = _cal.time
                        val out = SimpleDateFormat(
                            UtilDate.DATE_FORMATS[dateFormat],
                            Locale.getDefault()
                        ).format(date) + " [" + weekNames[_cal[Calendar.DAY_OF_WEEK] - 1] + "]"
                        _btnDate.text = out
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}