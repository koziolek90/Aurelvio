package com.kris.aurelvio.feature.dashboard.model

import kotlinx.datetime.LocalDate

enum class TimeRange {
    ONE_MONTH,
    SIX_MONTHS,
    ONE_YEAR,
    ALL
}

data class AssetItem(
    val id: String,
    val name: String,
    val category: String,
    val colorHex: String,
    val valueGrosze: Long,
    val profitGrosze: Long
)

data class NetWorthPoint(
    val date: LocalDate,
    val valueGrosze: Long
)

data class NetWorthSummary(
    val currentValueGrosze: Long,
    val periodChangeGrosze: Long,
    val valueGainGrosze: Long,
    val depositsGrosze: Long,
    val totalDepositsGrosze: Long,
    val totalProfitGrosze: Long
)

data class DashboardData(
    val summary: NetWorthSummary,
    val chartPoints: List<NetWorthPoint>,
    val topAssets: List<AssetItem>
)
