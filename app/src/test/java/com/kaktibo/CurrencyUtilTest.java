package com.kaktibo;

import com.kakeibo.Util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class CurrencyUtilTest {
    @Test
    public void testGetBDAmount_fraction_2() {
        BigDecimal actual = Util.getBDAmount(2018, 2);
        BigDecimal expected = BigDecimal.valueOf(20.18);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetBDAmount_fraction_1() {
        BigDecimal actual = Util.getBDAmount(920, 1);
        BigDecimal expected = BigDecimal.valueOf(92.0);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetBDAmount_fraction_0() {
        BigDecimal actual = Util.getBDAmount(920, 0);
        BigDecimal expected = BigDecimal.valueOf(920);
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetIntAmount_fraction_3() {
        int actual = Util.getIntAmount(BigDecimal.valueOf(150.200), 3);
        int expected = 150200;
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetIntAmount_fraction_2() {
        int actual = Util.getIntAmount(BigDecimal.valueOf(150.20), 2);
        int expected = 15020;
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetIntAmount_fraction_1() {
        int actual = Util.getIntAmount(BigDecimal.valueOf(150.0), 1);
        int expected = 1500;
        assertEquals("function failed", expected, actual);
    }

    @Test
    public void testGetIntAmount() {
        int actual = Util.getIntAmount(BigDecimal.valueOf(150), 0);
        int expected = 150;
        assertEquals("function failed", expected, actual);
    }
}