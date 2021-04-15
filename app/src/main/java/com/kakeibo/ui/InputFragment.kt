package com.kakeibo.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.Category
import com.kakeibo.data.Item
import com.kakeibo.databinding.FragmentInputBinding
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

/**
 * Created by T on 2015/09/14.
 */
class InputFragment : Fragment(), CategoryClickListener {
    private lateinit var _btnDate: Button
    private lateinit var _edtAmount: EditText
    private lateinit var _edtMemo: EditText

    private val _itemViewModel: ItemViewModel by activityViewModels()
    private val _categoryViewModel: CategoryViewModel by activityViewModels()

    companion object {
        private val TAG = InputFragment::class.java.simpleName

        fun newInstance(): InputFragment {
            val tabFragment1 = InputFragment()
            val args = Bundle()
            tabFragment1.arguments = args
            return tabFragment1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
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

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        _btnDate.text = getTodaysDateWithDay(
                SubApp.getDateFormat(R.string.pref_key_date_format),
                resources.getStringArray(R.array.week_name))
        _edtAmount.setText("")
        _edtMemo.setText("")
    }

    override fun onCategoryClicked(view: View, category: Category) {
        val result = UtilText.checkBeforeSave(_edtAmount.text.toString())
        if (!result.first) {
            Toast.makeText(context, requireActivity().getString(result.second), Toast.LENGTH_SHORT).show()
            return
        }

        val eventDate = UtilDate.getDBDate(_btnDate.text.toString().split(" ")[0], MainActivity.dateFormat)
        val updateDate = getTodaysDate(UtilDate.DATE_FORMAT_DB_KMS)
        val amount = when (category.color) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                BigDecimal(_edtAmount.text.toString())
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                BigDecimal(_edtAmount.text.toString()).negate()
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
        _btnDate.text = getTodaysDateWithDay(MainActivity.dateFormat, MainActivity.weekNames)
        hideKeyboard()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}