package com.kakeibo.ui.model

import java.math.BigDecimal
import java.math.RoundingMode

class Balance(var income: BigDecimal, var expense: BigDecimal) {
    var balance: BigDecimal? = null
        get() {
            field = income.subtract(expense)
            return field
        }
        private set

    fun addIncome(income: BigDecimal?) {
        this.income = this.income.add(income)
    }

    fun addExpense(expense: BigDecimal?) {
        this.expense = this.expense.add(expense)
    }

    fun inMinusOut(): Long {
        return if (balance!!.compareTo(BigDecimal(0)) < 0) -1 else if (balance!!.compareTo(BigDecimal(0)) > 0) 1 else 0
    }

    companion object {
        fun newInstance(fractionDigits: Int): Balance {
            return Balance(BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY),
                    BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY))
        }
    }
}