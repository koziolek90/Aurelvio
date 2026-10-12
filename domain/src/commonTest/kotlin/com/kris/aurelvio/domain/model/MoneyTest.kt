package com.kris.aurelvio.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoneyTest {

    @Test
    fun testZeroAndPositivityFlags() {
        val zero = Money.ZERO
        assertTrue(zero.isZero)
        assertFalse(zero.isPositive)
        assertFalse(zero.isNegative)

        val positive = Money(1500L)
        assertFalse(positive.isZero)
        assertTrue(positive.isPositive)
        assertFalse(positive.isNegative)

        val negative = Money(-500L)
        assertFalse(negative.isZero)
        assertFalse(negative.isPositive)
        assertTrue(negative.isNegative)
    }

    @Test
    fun testPlusAndMinus() {
        val a = Money(1000L)
        val b = Money(500L)

        assertEquals(Money(1500L), a + b)
        assertEquals(Money(500L), a - b)
        assertEquals(Money(-1000L), -a)
    }

    @Test
    fun testMultiplication() {
        val a = Money(1000L)

        assertEquals(Money(3000L), a * 3L)
        assertEquals(Money(1500L), a * 1.5)
    }

    @Test
    fun testComparison() {
        val a = Money(100L)
        val b = Money(200L)

        assertTrue(a < b)
        assertTrue(b > a)
        assertEquals(a, Money(100L))
    }

    @Test
    fun testAbs() {
        val negative = Money(-1234L)
        assertEquals(Money(1234L), negative.abs())

        val positive = Money(1234L)
        assertEquals(Money(1234L), positive.abs())
    }
}
