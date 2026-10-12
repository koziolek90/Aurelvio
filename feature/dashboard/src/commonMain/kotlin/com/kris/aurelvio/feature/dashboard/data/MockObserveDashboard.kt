package com.kris.aurelvio.feature.dashboard.data

import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.feature.dashboard.model.AssetItem
import com.kris.aurelvio.feature.dashboard.model.DashboardData
import com.kris.aurelvio.feature.dashboard.model.NetWorthPoint
import com.kris.aurelvio.feature.dashboard.model.NetWorthSummary
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class MockObserveDashboard(
    private val delayMs: Long = 400L,
    private val shouldFail: Boolean = false,
    private val isEmpty: Boolean = false
) : ObserveDashboard {

    override fun invoke(range: TimeRange): Flow<DashboardData> = flow {
        if (delayMs > 0) {
            delay(delayMs)
        }
        if (shouldFail) {
            throw IllegalStateException("Nie udało się pobrać danych z bazy.")
        }
        if (isEmpty) {
            emit(
                DashboardData(
                    summary = NetWorthSummary(
                        currentValue = Money.ZERO,
                        periodChange = Money.ZERO,
                        valueGain = Money.ZERO,
                        deposits = Money.ZERO,
                        totalDeposits = Money.ZERO,
                        totalProfit = Money.ZERO
                    ),
                    chartPoints = emptyList(),
                    topAssets = emptyList()
                )
            )
            return@flow
        }

        val today = LocalDate(2025, 2, 22)

        val (pointCount, stepDays) = when (range) {
            TimeRange.ONE_MONTH -> 30 to 1
            TimeRange.SIX_MONTHS -> 26 to 7
            TimeRange.ONE_YEAR -> 52 to 7
            TimeRange.ALL -> 60 to 14
        }

        val baseValue = 240_000L
        val targetValue = 285_430L
        val totalGrowth = targetValue - baseValue

        val points = mutableListOf<NetWorthPoint>()
        for (i in (pointCount - 1) downTo 0) {
            val date = today.minus(DatePeriod(days = i * stepDays))
            val progress = (pointCount - 1 - i).toDouble() / (pointCount - 1).coerceAtLeast(1)

            val linearPart = baseValue + (totalGrowth * progress).toLong()
            val fluctuation = (kotlin.math.sin(i * 0.8) * 1_200).toLong()
            val depositJump = if (i < pointCount / 2) 5_000L else 0L

            val calculatedVal = (linearPart + fluctuation + depositJump).coerceAtLeast(10_000L)
            val finalVal = if (i == 0) targetValue else calculatedVal

            points.add(NetWorthPoint(date, Money(finalVal)))
        }

        val periodChange = targetValue - points.first().value.amount
        val periodDeposits = when (range) {
            TimeRange.ONE_MONTH -> 2_500L
            TimeRange.SIX_MONTHS -> 8_000L
            TimeRange.ONE_YEAR -> 15_000L
            TimeRange.ALL -> 45_000L
        }
        val valueGain = periodChange - periodDeposits

        val summary = NetWorthSummary(
            currentValue = Money(targetValue),
            periodChange = Money(periodChange),
            valueGain = Money(valueGain),
            deposits = Money(periodDeposits),
            totalDeposits = Money(220_000L),
            totalProfit = Money(65_430L)
        )

        val assets = listOf(
            AssetItem(AssetId("1"), "ETF MSCI World", "Inwestycje", "#F59E0B", Money(97_350L), Money(11_350L)),
            AssetItem(AssetId("2"), "Lokata 12M", "Konta i lokaty", "#10B981", Money(60_000L), Money(2_400L)),
            AssetItem(AssetId("3"), "Konto oszczędnościowe", "Konta i lokaty", "#3B82F6", Money(48_200L), Money(1_200L)),
            AssetItem(AssetId("4"), "Akcje GPW", "Inwestycje", "#8B5CF6", Money(41_280L), Money(-1_820L)),
            AssetItem(AssetId("5"), "Obligacje EDO", "Obligacje", "#EC4899", Money(30_000L), Money(1_500L)),
            AssetItem(AssetId("6"), "Gotówka", "Gotówka", "#6B7280", Money(8_600L), Money.ZERO)
        )

        emit(
            DashboardData(
                summary = summary,
                chartPoints = points,
                topAssets = assets.take(5)
            )
        )
    }
}
