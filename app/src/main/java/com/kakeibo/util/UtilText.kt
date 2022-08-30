package com.kakeibo.util

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

fun isEmailValid(str: String): Boolean {
    return str.matches("[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(?:[A-Z]{2}|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum)\\b".toRegex())
}

/*
At least one upper case English letter
At least one lower case English letter
At least one digit
At least one special character
Minimum 8 in length
Maximum 32 in length
 */
fun isPasswordValid(str: String): Boolean {
    return str.matches("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,32}\$".toRegex())
}