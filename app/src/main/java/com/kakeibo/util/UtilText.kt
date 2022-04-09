package com.kakeibo.util

object UtilText {

    fun isAmountValid(str: String, fractionDigits: Int): Boolean {
        return when (fractionDigits) {
            0 -> {
                str.matches("([1-9]\\d*)?".toRegex())
            }
            2 -> {
                str.matches("((([1-9]\\d*)?\\d)([.]\\d{0,2})?)?".toRegex())
            }
            3 -> {
                str.matches("((([1-9]\\d*)?\\d)([.]\\d{0,3})?)?".toRegex())
            }
            else -> false
        }
    }

}