package com.kakeibo.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.kakeibo.R

object UtilText {

    fun checkBeforeSave(text: String?): Pair<Boolean, Int> {
        text?.let {
            if ("" == text) {
                return Pair(false, R.string.err_please_enter_amount)
            }
            if ("0" == text || "0.0" == text || "0.00" == text || "0.000" == text) {
                return Pair(false, R.string.err_amount_cannot_be_0)
            }
            if (!UtilCurrency.checkAmount(text)) {
                return Pair(false, R.string.err_amount_invalid)
            }
            return Pair(true, 0)
        }
        return Pair(false, R.string.err_amount_invalid) // text is null
    }

//    fun hideKeyboardFrom(context: Context, view: View) {
//        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
//    }
//
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
//
//    /**
//     * Shows the soft keyboard
//     */
//    fun showKeyboard(view: View) {
//        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        view.requestFocus()
//        inputMethodManager.showSoftInput(view, 0)
//    }
}