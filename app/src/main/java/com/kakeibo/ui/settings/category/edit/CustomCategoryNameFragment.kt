package com.kakeibo.ui.settings.category.edit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kakeibo.R
import com.kakeibo.databinding.FragmentSettingsCustomCategoryNameBinding
import com.kakeibo.util.UtilText

class CustomCategoryNameFragment : Fragment() {

    companion object {
        const val TAG_INT = 1

        fun newInstance(): CustomCategoryNameFragment {
            val fragment = CustomCategoryNameFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var _btnBack: Button
    private lateinit var _btnNext: Button
    private lateinit var _edtName: EditText

    private val _customCategoryViewModel: CustomCategoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSettingsCustomCategoryNameBinding.inflate(inflater, container, false)
        binding.customCategory = _customCategoryViewModel

        _btnBack = binding.root.findViewById(R.id.btn_back)
        _btnNext = binding.root.findViewById(R.id.btn_next)
        _edtName = binding.root.findViewById(R.id.edt_name)
        _btnBack.setOnClickListener(ButtonClickListener())
        _btnNext.setOnClickListener(ButtonClickListener())

        return binding.root
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.btn_back -> {
                    hideKeyboard()
                    (activity as CustomCategoryActivity).onBackPressed(TAG_INT)
                }
                R.id.btn_next -> {
                    if (checkBeforeProceed()) {
                        hideKeyboard()
                        _customCategoryViewModel.setName(_edtName.text.toString())
                        (activity as CustomCategoryActivity).onNextPressed(TAG_INT)
                    }
                }
            }
        }
    }

    private fun checkBeforeProceed(): Boolean {
        if (_edtName.text.toString().trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(activity, R.string.err_please_enter_name, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}