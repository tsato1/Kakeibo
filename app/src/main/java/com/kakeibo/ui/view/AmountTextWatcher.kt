//package com.kakeibo.ui.view
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.widget.EditText
//import com.kakeibo.R
//import com.kakeibo.SubApp
//
//class AmountTextWatcher(private val edtAmount: EditText) : TextWatcher {
//
//    private val _fractionDigits = SubApp.getFractionDigits(R.string.pref_key_fraction_digits)
//
//    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//    override fun afterTextChanged(editable: Editable) {
//
//        val str = editable.toString()
//        val length = str.length
//
//        if (_fractionDigits == 0) {
//            if (length >= 1 && (str[length - 1] == '.' || str[length - 1] == ',')) {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//            }
//            if (length >= 1 && str[0] == '0') {
//                edtAmount.setText("")
//            }
//            return
//        }
//
//        if (length > 1) {
//            // consecutive 0s from the beginning is not allowed
//            if (str[0] == '0' && str[1] == '0') {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//            }
//            // containing two periods, commas, a period after a comma, a comma after a period are not allowed
//            if (str[length - 1] == '.' && (str.substring(0, length - 1).contains('.')
//                        || str.substring(0, length - 1).contains(','))) {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//                return
//            }
//            if (str[length - 1] == ',' && (str.substring(0, length - 1).contains('.')
//                        || str.substring(0, length - 1).contains(','))) {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//                return
//            }
//            // handling digits part
//            if (str.contains(".") &&
//                str.substring(str.indexOf('.')).length > _fractionDigits + 1) {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//            }
//            // handling digits part
//            if (str.contains(",") &&
//                str.substring(str.indexOf(',')).length > _fractionDigits + 1) {
//                edtAmount.setText(str.substring(0, length - 1))
//                edtAmount.setSelection(edtAmount.text.length)
//            }
//        }
//    }
//}