package com.kris.aurelvio.feature.dashboard.ui.format

import kotlin.math.abs

/**
 * Temporary money formatter (Long in grosze -> formatted PLN string).
 *
 * Format: "285 430,50 zł"
 * Non-breaking space (\u00A0) for thousand separators, comma for decimal.
 * Includes + and − (\u2212) signs for change values when explicitly requested.
 */
object MoneyFormatter {

    fun format(
        amountGrosze: Long,
        includeSign: Boolean = false,
        currencySymbol: String = "zł"
    ): String {
        val isNegative = amountGrosze < 0
        val absGrosze = abs(amountGrosze)
        val zloty = absGrosze / 100
        val grosze = absGrosze % 100

        val formattedZloty = formatThousands(zloty)
        val formattedGrosze = grosze.toString().padStart(2, '0')

        val signPrefix = when {
            isNegative -> "−" // Unicode minus U+2212
            includeSign && amountGrosze > 0 -> "+"
            else -> ""
        }

        return "$signPrefix$formattedZloty,$formattedGrosze\u00A0$currencySymbol"
    }

    private fun formatThousands(value: Long): String {
        val raw = value.toString()
        val result = StringBuilder()
        var count = 0
        for (i in raw.length - 1 downTo 0) {
            if (count > 0 && count % 3 == 0) {
                result.append('\u00A0') // Non-breaking space
            }
            result.append(raw[i])
            count++
        }
        return result.reverse().toString()
    }
}
