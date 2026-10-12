package com.kris.aurelvio.feature.dashboard.data

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
                    summary = NetWorthSummary(0, 0, 0, 0, 0, 0),
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

        val baseValue = 240_000_00L
        val targetValue = 285_430_50L
        val totalGrowth = targetValue - baseValue

        val points = mutableListOf<NetWorthPoint>()
        for (i in (pointCount - 1) downTo 0) {
            val date = today.minus(DatePeriod(days = i * stepDays))
            val progress = (pointCount - 1 - i).toDouble() / (pointCount - 1).coerceAtLeast(1)

            val linearPart = baseValue + (totalGrowth * progress).toLong()
            val fluctuation = (kotlin.math.sin(i * 0.8) * 1_200_00).toLong()
            val depositJump = if (i < pointCount / 2) 5_000_00L else 0L

            val calculatedVal = (linearPart + fluctuation + depositJump).coerceAtLeast(10_000_00L)
            val finalVal = if (i == 0) targetValue else calculatedVal

            points.add(NetWorthPoint(date, finalVal))
        }

        val periodChange = targetValue - points.first().valueGrosze
        val periodDeposits = when (range) {
            TimeRange.ONE_MONTH -> 2_500_00L
            TimeRange.SIX_MONTHS -> 8_000_00L
            TimeRange.ONE_YEAR -> 15_000_00L
            TimeRange.ALL -> 45_000_00L
        }
        val valueGain = periodChange - periodDeposits

        val summary = NetWorthSummary(
            currentValueGrosze = targetValue,
            periodChangeGrosze = periodChange,
            valueGainGrosze = valueGain,
            depositsGrosze = periodDeposits,
            totalDepositsGrosze = 220_000_00L,
            totalProfitGrosze = 65_430_50L
        )

        val assets = listOf(
            AssetItem("1", "ETF MSCI World", "Inwestycje", "#F59E0B", 97_350_00L, 11_350_00L),
            AssetItem("2", "Lokata 12M", "Konta i lokaty", "#10B981", 60_000_00L, 2_400_00L),
            AssetItem("3", "Konto oszczędnościowe", "Konta i lokaty", "#3B82F6", 48_200_00L, 1_200_00L),
            AssetItem("4", "Akcje GPW", "Inwestycje", "#8B5CF6", 41_280_50L, -1_820_00L),
            AssetItem("5", "Obligacje EDO", "Obligacje", "#EC4899", 30_000_00L, 1_500_00L),
            AssetItem("6", "Gotówka", "Gotówka", "#6B7280", 8_600_00L, 0L)
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
