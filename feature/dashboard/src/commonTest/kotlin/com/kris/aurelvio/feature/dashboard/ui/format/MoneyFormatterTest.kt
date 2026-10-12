package com.kris.aurelvio.feature.dashboard.ui.format

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatterTest {

    @Test
    fun testFormatZeroGrosze() {
        val result = MoneyFormatter.format(0L)
        assertEquals("0,00\u00A0zł", result)
    }

    @Test
    fun testFormatPositiveGrosze() {
        val result = MoneyFormatter.format(285_430_50L)
        assertEquals("285\u00A0430,50\u00A0zł", result)
    }

    @Test
    fun testFormatNegativeGrosze() {
        val result = MoneyFormatter.format(-1_820_00L)
        assertEquals("−1\u00A0820,00\u00A0zł", result)
    }

    @Test
    fun testFormatIncludeSignForPositive() {
        val result = MoneyFormatter.format(11_350_00L, includeSign = true)
        assertEquals("+11\u00A0350,00\u00A0zł", result)
    }

    @Test
    fun testFormatIncludeSignForNegative() {
        val result = MoneyFormatter.format(-500_00L, includeSign = true)
        assertEquals("−500,00\u00A0zł", result)
    }

    @Test
    fun testFormatLargeMillions() {
        val result = MoneyFormatter.format(1_234_567_89L)
        assertEquals("1\u00A0234\u00A0567,89\u00A0zł", result)
    }
}
