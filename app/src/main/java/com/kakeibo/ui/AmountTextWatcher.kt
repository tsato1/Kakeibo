package com.kakeibo.ui

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.kakeibo.R
import com.kakeibo.SubApp
import java.util.*

class AmountTextWatcher(edtAmount: EditText) : TextWatcher {
    private val mEdtAmount: EditText
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
        val str = editable.toString()
        val length = str.length
        if (_fractionDigits == 0) {
            if (length > 1 && str[length - 1] == '.') {
                mEdtAmount.setText(str.substring(0, length - 1))
                mEdtAmount.setSelection(mEdtAmount.text.length)
            }
            return
        }
        if (length == 1) {
            if (str[0] == '.') {
                mEdtAmount.setText("")
            }
        } else if (length > 1) {
            if (str[0] == '0' && str[1] != '.') {
                mEdtAmount.setText(str.substring(0, length - 1))
                mEdtAmount.setSelection(mEdtAmount.text.length)
                return
            }
            if (str[length - 1] == '.' && secondTime(str)) {
                mEdtAmount.setText(str.substring(0, length - 1))
                mEdtAmount.setSelection(mEdtAmount.text.length)
                return
            }
            if (str.contains(".") &&
                    str.substring(str.indexOf('.')).length > _fractionDigits + 1) {
                mEdtAmount.setText(str.substring(0, length - 1))
                mEdtAmount.setSelection(mEdtAmount.text.length)
            }
        }
    }

    private fun secondTime(str: String): Boolean {
        val set: MutableSet<Char> = HashSet()
        for (i in str.indices) {
            if (set.contains(str[i]) && str[i] == '.') {
                return true
            }
            set.add(str[i])
        }
        return false
    }

    companion object {
        private var _fractionDigits: Int = SubApp.getFractionDigits(R.string.pref_key_fraction_digits)
    }

    init {
        _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits)
        mEdtAmount = edtAmount
    }
}