package com.kakeibo;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Balance {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;

    static Balance newInstance(int fractionDigits) {
        return new Balance(new BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY),
                new BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY));
    }

    private Balance (BigDecimal income, BigDecimal expense) {
        this.income = income;
        this.expense = expense;
    }

    BigDecimal getIncome () {
        return income;
    }

    BigDecimal getExpense () {
        return expense;
    }

    BigDecimal getBalance () {
        balance = income.subtract(expense);
        return balance;
    }

    void addIncome (BigDecimal income) {
        this.income = this.income.add(income);
    }

    void addExpense (BigDecimal expense) {
        this.expense = this.expense.add(expense);
    }

    long inMinusOut () {
        if (getBalance().compareTo(new BigDecimal(0)) < 0) return -1;
        else if (getBalance().compareTo(new BigDecimal(0)) > 0) return 1;
        else return 0;
    }
}
