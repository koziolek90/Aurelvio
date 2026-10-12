package com.kris.aurelvio.domain.model

import kotlin.jvm.JvmInline
import kotlin.math.abs

/**
 * Monetary amount stored in whole currency units (e.g. 1 PLN = Money(1L)).
 * The app tracks integer asset values without cents / minor units.
 *
 * Immutable value class wrapping a [Long] to guarantee type safety and prevent
 * floating-point inaccuracies.
 */
@JvmInline
value class Money(val amount: Long) : Comparable<Money> {

    val isZero: Boolean get() = amount == 0L
    val isPositive: Boolean get() = amount > 0L
    val isNegative: Boolean get() = amount < 0L

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    operator fun unaryMinus(): Money = Money(-amount)
    operator fun times(factor: Long): Money = Money(amount * factor)
    operator fun times(factor: Double): Money = Money((amount * factor).toLong())

    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    fun abs(): Money = Money(abs(amount))

    companion object {
        val ZERO = Money(0L)
    }
}
