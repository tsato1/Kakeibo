package com.kakeibo.util

import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan

object UtilCurrency {
    const val CURRENCY_NONE = "---"
    const val CURRENCY_OLD = "==="

    fun getSignedAmount(amount: String): SpannableString {
        return when {
            amount.substring(0, 1) == "-" -> { // negative amount
                val out = SpannableString(amount)
                out.setSpan(ForegroundColorSpan(Color.RED), 0, 1, 0)
                out
            }
            amount == "0" -> { // amount = 0
                SpannableString(amount)
            }
            else -> { // positive amount
                val out = SpannableString("+$amount")
                out.setSpan(ForegroundColorSpan(Color.BLUE), 0, 1, 0)
                out
            }
        }
    }

    fun getSignedAmountColon(span1: String, span2: String): CharSequence {
        return TextUtils.concat(SpannableString(span1), getSignedAmount(span2))
    }
}