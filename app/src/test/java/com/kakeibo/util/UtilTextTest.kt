package com.kakeibo.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UtilTextTest {

    @Test
    fun isEmailValidTest() {
        assertTrue(isEmailValid("asdf@asdf.com"))
    }

    @Test
    fun isPasswordValidTest() {
        assertTrue(isPasswordValid("aA123!4567"))
    }

    @Test
    fun isEmailAndPasswordNotValid() {
        assertFalse(!isEmailValid("asdf@asdf.com") || !isPasswordValid("aA123!4567"))
    }

    @Test
    fun isEmailAndPasswordValid() {
        assertTrue(isEmailValid("asdf@asdf.com") && isPasswordValid("aA123!4567"))
    }
}