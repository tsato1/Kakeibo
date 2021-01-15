//package com.kakeibo.ui.items;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//
//public class Balance {
//    private BigDecimal income;
//    private BigDecimal expense;
//    private BigDecimal balance;
//
//    public static Balance newInstance(int fractionDigits) {
//        return new Balance(new BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY),
//                new BigDecimal(0).setScale(fractionDigits, RoundingMode.UNNECESSARY));
//    }
//
//    public Balance (BigDecimal income, BigDecimal expense) {
//        this.income = income;
//        this.expense = expense;
//    }
//
//    public BigDecimal getIncome () {
//        return income;
//    }
//
//    public BigDecimal getExpense () {
//        return expense;
//    }
//
//    public BigDecimal getBalance () {
//        balance = income.subtract(expense);
//        return balance;
//    }
//
//    public void addIncome (BigDecimal income) {
//        this.income = this.income.add(income);
//    }
//
//    public void addExpense (BigDecimal expense) {
//        this.expense = this.expense.add(expense);
//    }
//
//    public long inMinusOut () {
//        if (getBalance().compareTo(new BigDecimal(0)) < 0) return -1;
//        else if (getBalance().compareTo(new BigDecimal(0)) > 0) return 1;
//        else return 0;
//    }
//}
