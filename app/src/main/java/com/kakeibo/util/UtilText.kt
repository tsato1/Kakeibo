package com.kakeibo.util

import com.kakeibo.R

object UtilText {

    fun checkBeforeSave(text: String?): Pair<Boolean, Int> {
        text?.let {
            if ("" == it) {
                return Pair(false, R.string.err_please_enter_amount)
            }
            if ("." == it || "," == it) {
                return Pair(false, R.string.err_amount_invalid)
            }

            val text2 = it.replace(',', '.')

            if (text2.toFloat() == 0f) {
                return Pair(false, R.string.err_amount_cannot_be_0)
            }
            if (!checkAmount(text2)) {
                return Pair(false, R.string.err_amount_invalid)
            }
            return Pair(true, 0)
        }
        return Pair(false, R.string.err_amount_invalid) // text is null
    }

    fun checkAmount(str: String): Boolean {
        return str.matches("\\d+(\\.\\d+)?".toRegex())
    }
}