package com.kris.aurelvio.feature.dashboard.model

import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Money
import kotlinx.datetime.LocalDate

enum class TimeRange {
    ONE_MONTH,
    SIX_MONTHS,
    ONE_YEAR,
    ALL,
}

data class AssetItem(
    val id: AssetId,
    val name: String,
    val category: String,
    val colorHex: String,
    val value: Money,
    val profit: Money,
)

data class NetWorthPoint(
    val date: LocalDate,
    val value: Money,
)

data class NetWorthSummary(
    val currentValue: Money,
    val periodChange: Money,
    val valueGain: Money,
    val deposits: Money,
    val totalDeposits: Money,
    val totalProfit: Money,
)

data class DashboardData(
    val summary: NetWorthSummary,
    val chartPoints: List<NetWorthPoint>,
    val topAssets: List<AssetItem>,
)
