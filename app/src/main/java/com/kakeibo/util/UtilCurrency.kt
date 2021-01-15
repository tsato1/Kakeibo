package com.kakeibo.util

import com.kakeibo.R
import com.kakeibo.SubApp
import java.math.BigDecimal

object UtilCurrency {
    private val TAG = UtilCurrency::class.java.simpleName
    const val CURRENCY_NONE = "---"
    const val CURRENCY_OLD = "==="
    fun getLongFromBigDecimal(amount: BigDecimal): Long {
        return amount.multiply(BigDecimal.valueOf(1000)).toLong()
    }

    fun getLongAmountFromBigDecimal(amount: BigDecimal, currencyFractionDigits: Int): Long {
        if (currencyFractionDigits == 0) {
            return amount.toInt().toLong()
        } else if (currencyFractionDigits == 1) {
            return amount.multiply(BigDecimal.valueOf(10)).toLong()
        } else if (currencyFractionDigits == 2) {
            return amount.multiply(BigDecimal.valueOf(100)).toLong()
        } else if (currencyFractionDigits == 3) {
            return amount.multiply(BigDecimal.valueOf(1000)).toLong()
        }
        return -1
    }

    fun getBigDecimalFromLong(amount: Long): BigDecimal {
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(1000),
                        SubApp.getFractionDigits(R.string.pref_key_fraction_digits),
                        BigDecimal.ROUND_HALF_UP)
    }

    fun checkAmount(str: String): Boolean {
        return str.matches("\\d+(\\.\\d+)?".toRegex())
    }
}