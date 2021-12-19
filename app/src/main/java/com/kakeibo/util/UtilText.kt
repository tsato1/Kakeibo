package com.kakeibo.util

object UtilText {

    //todo: placement of a dot (decimal)
    fun isAmountValid(str: String): Boolean {
        return str.matches("\\d+(\\.\\d+)?".toRegex())
    }

}