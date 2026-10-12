package com.kris.aurelvio.feature.dashboard.ui.format

import com.kris.aurelvio.domain.model.Money
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatterTest {

    @Test
    fun testFormatZeroAmount() {
        val result = MoneyFormatter.format(Money.ZERO)
        assertEquals("0\u00A0zł", result)
    }

    @Test
    fun testFormatPositiveAmount() {
        val result = MoneyFormatter.format(Money(285_430L))
        assertEquals("285\u00A0430\u00A0zł", result)
    }

    @Test
    fun testFormatNegativeAmount() {
        val result = MoneyFormatter.format(Money(-1_820L))
        assertEquals("−1\u00A0820\u00A0zł", result)
    }

    @Test
    fun testFormatIncludeSignForPositive() {
        val result = MoneyFormatter.format(Money(11_350L), includeSign = true)
        assertEquals("+11\u00A0350\u00A0zł", result)
    }

    @Test
    fun testFormatIncludeSignForNegative() {
        val result = MoneyFormatter.format(Money(-500L), includeSign = true)
        assertEquals("−500\u00A0zł", result)
    }

    @Test
    fun testFormatLargeMillions() {
        val result = MoneyFormatter.format(Money(1_234_567L))
        assertEquals("1\u00A0234\u00A0567\u00A0zł", result)
    }
}
