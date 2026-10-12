package com.kris.aurelvio.feature.dashboard.ui.format

import com.kris.aurelvio.domain.model.Money
import kotlin.math.abs

/**
 * Money formatter (Money -> formatted PLN string, e.g. "285 430 zł").
 *
 * Non-breaking space (\u00A0) for thousand separators.
 * Includes + and − (\u2212) signs for change values when explicitly requested.
 */
object MoneyFormatter {

    fun format(
        money: Money,
        includeSign: Boolean = false,
        currencySymbol: String = "zł",
    ): String {
        val amount = money.amount
        val isNegative = amount < 0
        val absAmount = abs(amount)

        val formattedAmount = formatThousands(absAmount)

        val signPrefix = when {
            isNegative -> "−" // Unicode minus U+2212
            includeSign && (amount > 0) -> "+"
            else -> ""
        }

        return "$signPrefix$formattedAmount\u00A0$currencySymbol"
    }

    private fun formatThousands(value: Long): String {
        val raw = value.toString()
        val result = StringBuilder()
        for ((count, i) in (raw.length - 1 downTo 0).withIndex()) {
            if (count > 0 && count % 3 == 0) {
                result.append('\u00A0') // Non-breaking space
            }
            result.append(raw[i])
        }
        return result.reverse().toString()
    }
}
