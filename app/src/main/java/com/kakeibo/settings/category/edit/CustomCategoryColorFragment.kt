package com.kakeibo.settings.category.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kakeibo.R
import com.kakeibo.databinding.FragmentSettingsCustomCategoryColorBinding
import com.kakeibo.util.UtilCategory

class CustomCategoryColorFragment : Fragment() {

    companion object {
        const val TAG_INT = 0

        fun newInstance(): CustomCategoryColorFragment {
            val fragment = CustomCategoryColorFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _btnIncome: Button
    private lateinit var _btnExpense: Button
    private lateinit var _btnBack: Button
    private lateinit var _btnNext: Button
    private lateinit var _imvIncomeOverlay: ImageView
    private lateinit var _imvExpenseOverlay: ImageView
    private lateinit var _txvDescription: TextView

    private val _customCategoryViewModel: CustomCategoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSettingsCustomCategoryColorBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.customCategory = _customCategoryViewModel
        val view = binding.root
        findViews(view)
        return view
    }

    private fun findViews(view: View) {
        _btnIncome = view.findViewById(R.id.btn_income)
        _btnExpense = view.findViewById(R.id.btn_expense)
        _btnBack = view.findViewById(R.id.btn_back)
        _btnNext = view.findViewById(R.id.btn_next)
        _imvIncomeOverlay = view.findViewById(R.id.imv_category_add_in)
        _imvExpenseOverlay = view.findViewById(R.id.imv_category_add_ex)
        _txvDescription = view.findViewById(R.id.txv_description)
        _btnIncome.setOnClickListener(ButtonClickListener())
        _btnExpense.setOnClickListener(ButtonClickListener())
        _btnBack.setOnClickListener(ButtonClickListener())
        _btnNext.setOnClickListener(ButtonClickListener())
        val str = getString(R.string.max_number_of_categories_colon) + UtilCategory.NUM_MAX_CUSTOM_CATEGORY
        _txvDescription.setText(str)
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_income -> {
                    _customCategoryViewModel.setColor(UtilCategory.CATEGORY_COLOR_INCOME)
                    selectColor()
                }
                R.id.btn_expense -> {
                    _customCategoryViewModel.setColor(UtilCategory.CATEGORY_COLOR_EXPENSE)
                    selectColor()
                }
                R.id.btn_back -> (activity as CustomCategoryActivity).onBackPressed(TAG_INT)
                R.id.btn_next -> {
                    if (_customCategoryViewModel.color.value == -1) {
                        Toast.makeText(activity, getString(R.string.err_nothing_selected_select_one), Toast.LENGTH_SHORT).show()
                        return
                    }
                    (activity as CustomCategoryActivity).onNextPressed(TAG_INT)
                }
            }
        }
    }

    private fun selectColor() {
        when (_customCategoryViewModel.color.value) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                _imvIncomeOverlay.visibility = View.VISIBLE
                _imvExpenseOverlay.visibility = View.INVISIBLE
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                _imvIncomeOverlay.visibility = View.INVISIBLE
                _imvExpenseOverlay.visibility = View.VISIBLE
            }
            -1 -> {
                _imvIncomeOverlay.visibility = View.INVISIBLE
                _imvExpenseOverlay.visibility = View.INVISIBLE
            }
        }
    }
}