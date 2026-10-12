package com.kris.aurelvio.feature.dashboard.data

import com.kris.aurelvio.feature.dashboard.model.DashboardData
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import kotlinx.coroutines.flow.Flow

fun interface ObserveDashboard {
    operator fun invoke(range: TimeRange): Flow<DashboardData>
}
