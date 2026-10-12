package com.kris.aurelvio.feature.dashboard.presentation

import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.feature.dashboard.model.DashboardData
import com.kris.aurelvio.feature.dashboard.model.TimeRange

data class DashboardState(
    val range: TimeRange = TimeRange.ONE_MONTH,
    val content: DashboardContent = DashboardContent.Loading
)

sealed interface DashboardContent {
    data object Loading : DashboardContent
    data object Empty : DashboardContent
    data class Data(val data: DashboardData) : DashboardContent
    data class Error(val message: String) : DashboardContent
}

sealed interface DashboardAction {
    data class RangeSelected(val range: TimeRange) : DashboardAction
    data object AddEntryClicked : DashboardAction
    data object UpdateValuesClicked : DashboardAction
    data object SeeAllAssetsClicked : DashboardAction
    data class AssetClicked(val id: AssetId) : DashboardAction
    data object Retry : DashboardAction
}

sealed interface DashboardEffect {
    data object NavigateToAddEntry : DashboardEffect
    data object NavigateToQuickUpdate : DashboardEffect
    data object NavigateToAssets : DashboardEffect
    data class NavigateToAsset(val id: AssetId) : DashboardEffect
}
