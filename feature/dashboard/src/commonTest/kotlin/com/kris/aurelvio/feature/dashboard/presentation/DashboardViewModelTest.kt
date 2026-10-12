package com.kris.aurelvio.feature.dashboard.presentation

import app.cash.turbine.test
import com.kris.aurelvio.feature.dashboard.data.MockObserveDashboard
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateEmitsLoadingThenData() = runTest {
        val mockData = MockObserveDashboard(delayMs = 0L)
        val viewModel = DashboardViewModel(mockData)

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(TimeRange.ONE_MONTH, initialState.range)

            val loadedState = awaitItem()
            assertIs<DashboardContent.Data>(loadedState.content)
            val data = (loadedState.content as DashboardContent.Data).data
            assertTrue(data.topAssets.isNotEmpty())
            assertTrue(data.chartPoints.isNotEmpty())
        }
    }

    @Test
    fun testRangeSelectedReloadsData() = runTest {
        val mockData = MockObserveDashboard(delayMs = 0L)
        val viewModel = DashboardViewModel(mockData)

        viewModel.state.test {
            awaitItem()
            val loaded1M = awaitItem()
            assertEquals(TimeRange.ONE_MONTH, loaded1M.range)

            viewModel.onAction(DashboardAction.RangeSelected(TimeRange.ONE_YEAR))

            val loadingYear = awaitItem()
            assertEquals(TimeRange.ONE_YEAR, loadingYear.range)
            assertIs<DashboardContent.Loading>(loadingYear.content)

            val loadedYear = awaitItem()
            assertEquals(TimeRange.ONE_YEAR, loadedYear.range)
            assertIs<DashboardContent.Data>(loadedYear.content)
        }
    }

    @Test
    fun testErrorAndRetry() = runTest {
        val failingMock = MockObserveDashboard(delayMs = 0L, shouldFail = true)
        val viewModel = DashboardViewModel(failingMock)

        viewModel.state.test {
            awaitItem()
            val errorState = awaitItem()
            assertIs<DashboardContent.Error>(errorState.content)

            val successMock = MockObserveDashboard(delayMs = 0L, shouldFail = false)
            val retryViewModel = DashboardViewModel(successMock)

            retryViewModel.state.test {
                awaitItem()
                val recoveredState = awaitItem()
                assertIs<DashboardContent.Data>(recoveredState.content)
            }
        }
    }

    @Test
    fun testEmptyState() = runTest {
        val emptyMock = MockObserveDashboard(delayMs = 0L, isEmpty = true)
        val viewModel = DashboardViewModel(emptyMock)

        viewModel.state.test {
            awaitItem()
            val emptyState = awaitItem()
            assertIs<DashboardContent.Empty>(emptyState.content)
        }
    }

    @Test
    fun testActionEffectsNavigation() = runTest {
        val mockData = MockObserveDashboard(delayMs = 0L)
        val viewModel = DashboardViewModel(mockData)

        viewModel.effects.test {
            viewModel.onAction(DashboardAction.AddEntryClicked)
            assertEquals(DashboardEffect.NavigateToAddEntry, awaitItem())

            viewModel.onAction(DashboardAction.UpdateValuesClicked)
            assertEquals(DashboardEffect.NavigateToQuickUpdate, awaitItem())

            viewModel.onAction(DashboardAction.SeeAllAssetsClicked)
            assertEquals(DashboardEffect.NavigateToAssets, awaitItem())

            viewModel.onAction(DashboardAction.AssetClicked("123"))
            assertEquals(DashboardEffect.NavigateToAsset("123"), awaitItem())
        }
    }
}
