package com.kaktibo;

import com.kakeibo.util.UtilCurrency;
import com.kakeibo.util.UtilDate;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigDecimal;

public class UtilCurrencyTest {
    @Test
    public void test_getIntAmountFromBigDecimal_0() {
        int expected = 2018;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(BigDecimal.valueOf(2018),
                0);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void test_getIntAmountFromBigDecimal_1() {
        int expected = 2018;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(BigDecimal.valueOf(201.8),
                1);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void test_getIntAmountFromBigDecimal_2() {
        int expected = 2018;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(BigDecimal.valueOf(20.18),
                2);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void test_getIntAmountFromBigDecimal_3() {
        int expected = 2010;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(BigDecimal.valueOf(20.1),
                2);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void test_getIntAmountFromBigDecimal_4() {
        int expected = 20180;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(BigDecimal.valueOf(20.18),
                3);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void test_getIntAmountFromBigDecimal_5() {
        int expected = 20180;
        int actual = UtilCurrency.getIntAmountFromBigDecimal(new BigDecimal("20.18"),
                3);
        assertEquals("function failed", expected, actual);
    }


}