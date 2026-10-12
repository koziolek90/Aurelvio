package com.kris.aurelvio.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kris.aurelvio.feature.dashboard.data.ObserveDashboard
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val observeDashboard: ObserveDashboard
) : ViewModel() {

    private val selectedRangeFlow = MutableStateFlow(TimeRange.ONE_MONTH)

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effects = Channel<DashboardEffect>(Channel.BUFFERED)
    val effects: Flow<DashboardEffect> = _effects.receiveAsFlow()

    init {
        observeData()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.RangeSelected -> {
                if (selectedRangeFlow.value != action.range) {
                    selectedRangeFlow.value = action.range
                }
            }
            DashboardAction.AddEntryClicked -> {
                _effects.trySend(DashboardEffect.NavigateToAddEntry)
            }
            DashboardAction.UpdateValuesClicked -> {
                _effects.trySend(DashboardEffect.NavigateToQuickUpdate)
            }
            DashboardAction.SeeAllAssetsClicked -> {
                _effects.trySend(DashboardEffect.NavigateToAssets)
            }
            is DashboardAction.AssetClicked -> {
                _effects.trySend(DashboardEffect.NavigateToAsset(action.id))
            }
            DashboardAction.Retry -> {
                observeData()
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            selectedRangeFlow
                .flatMapLatest { range ->
                    observeDashboard(range)
                        .onStart {
                            _state.value = DashboardState(range = range, content = DashboardContent.Loading)
                        }
                        .catch { throwable ->
                            _state.value = DashboardState(
                                range = range,
                                content = DashboardContent.Error(throwable.message ?: "Wystąpił błąd.")
                            )
                        }
                }
                .collect { data ->
                    val content = if (data.chartPoints.isEmpty() && data.topAssets.isEmpty()) {
                        DashboardContent.Empty
                    } else {
                        DashboardContent.Data(data)
                    }
                    _state.value = DashboardState(range = selectedRangeFlow.value, content = content)
                }
        }
    }
}
