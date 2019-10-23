package com.kakeibo.util;

import java.math.BigDecimal;

public class UtilCurrency {
    private final static String TAG = UtilCurrency.class.getSimpleName();
    public final static String CURRENCY_NONE = "---";
    public final static String CURRENCY_OLD = "===";

    public static long getLongAmountFromBigDecimal(BigDecimal amount, int currencyFractionDigits) {
        if (currencyFractionDigits == 0) {
            return amount.intValue();
        } else if (currencyFractionDigits == 1) {
            return amount.multiply(BigDecimal.valueOf(10)).longValue();
        } else if (currencyFractionDigits == 2) {
            return amount.multiply(BigDecimal.valueOf(100)).longValue();
        } else if (currencyFractionDigits == 3) {
            return amount.multiply(BigDecimal.valueOf(1000)).longValue();
        }

        return -1;
    }

    public static BigDecimal getBigDecimalAmountFromInt(int amount, int currencyFractionDigits) {
        return BigDecimal.valueOf(amount, currencyFractionDigits);
    }

    public static boolean checkAmount(String str) {
        return str.matches("\\d+(\\.\\d+)?");

//        try (Scanner scanner = new Scanner(System.in)) {
//            System.out.println("Enter an integer : ");
//            if (scanner.hasNextInt()) {
//                System.out.println("You entered : " + scanner.nextInt());
//            } else {
//                System.out.println("The input is not an integer");
//            }
//        }
    }
}
