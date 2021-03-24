package com.kakeibo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.ItemStatus
import com.kakeibo.databinding.FragmentInputBinding
import com.kakeibo.ui.adapter.CategoryGridAdapter
import com.kakeibo.ui.viewmodel.CategoryStatusViewModel
import com.kakeibo.ui.listener.CategoryClickListener
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.viewmodel.ItemStatusViewModel
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

    private val _itemStatusViewModel: ItemStatusViewModel by activityViewModels()
    private val _categoryStatusViewModel: CategoryStatusViewModel by activityViewModels()

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

        val recyclerView: RecyclerView = view.findViewById(R.id.rcv_grid)
        val categoryGridAdapter = CategoryGridAdapter(this)
        recyclerView.adapter = categoryGridAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, MainActivity.numColumns)
        _categoryStatusViewModel.dsp.observe(viewLifecycleOwner, {
            categoryGridAdapter.setCategoryStatuses(it)
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

    override fun onCategoryClicked(view: View, category: CategoryStatus) {
        val result = UtilText.checkBeforeSave(_edtAmount.text.toString())
        if (!result.first) {
            Toast.makeText(context, requireActivity().getString(result.second), Toast.LENGTH_SHORT).show()
            return
        }

        val eventDate = UtilDate.getDBDate(_btnDate.text.toString().split(" ")[0], MainActivity.dateFormat)
        val updateDate = getTodaysDate(UtilDate.DATE_FORMAT_DB_HMS)
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
        val itemStatus = ItemStatus(
                amount,
                UtilCurrency.CURRENCY_NONE,
                category.code,
                _edtMemo.text.toString(),
                eventDate,
                updateDate
        )

        _itemStatusViewModel.insert(itemStatus)
        Toast.makeText(activity, resources.getString(R.string.msg_item_successfully_saved), Toast.LENGTH_SHORT).show()

        (activity as MainActivity).onItemSaved(eventDate)
        _btnDate.text = getTodaysDateWithDay(MainActivity.dateFormat, MainActivity.weekNames)
    }
}