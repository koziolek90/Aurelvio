package com.kris.aurelvio.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.app_name
import aurelvio.feature.dashboard.generated.resources.dashboard_empty_action
import aurelvio.feature.dashboard.generated.resources.dashboard_empty_description
import aurelvio.feature.dashboard.generated.resources.dashboard_empty_title
import aurelvio.feature.dashboard.generated.resources.dashboard_error_retry
import aurelvio.feature.dashboard.generated.resources.dashboard_error_title
import com.kris.aurelvio.core.designsystem.components.AurelvioCard
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.feature.dashboard.data.MockObserveDashboard
import com.kris.aurelvio.feature.dashboard.model.AssetItem
import com.kris.aurelvio.feature.dashboard.model.DashboardData
import com.kris.aurelvio.feature.dashboard.model.NetWorthPoint
import com.kris.aurelvio.feature.dashboard.model.NetWorthSummary
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import com.kris.aurelvio.feature.dashboard.presentation.DashboardAction
import com.kris.aurelvio.feature.dashboard.presentation.DashboardContent
import com.kris.aurelvio.feature.dashboard.presentation.DashboardEffect
import com.kris.aurelvio.feature.dashboard.presentation.DashboardState
import com.kris.aurelvio.feature.dashboard.presentation.DashboardViewModel
import com.kris.aurelvio.feature.dashboard.ui.components.ActionButtonsRow
import com.kris.aurelvio.feature.dashboard.ui.components.HeroNetWorthCard
import com.kris.aurelvio.feature.dashboard.ui.components.NetWorthChart
import com.kris.aurelvio.feature.dashboard.ui.components.RangeSelector
import com.kris.aurelvio.feature.dashboard.ui.components.SummaryCardRow
import com.kris.aurelvio.feature.dashboard.ui.components.TopAssetsSection
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun DashboardRoute(
    onEffect: (DashboardEffect) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel { DashboardViewModel(MockObserveDashboard()) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            onEffect(effect)
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AurelvioTheme.colors

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .padding(horizontal = AurelvioSpacing.l),
                contentPadding = PaddingValues(vertical = AurelvioSpacing.l),
                verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.xl),
            ) {
                // Header
                item {
                    Text(
                        text = stringResource(Res.string.app_name).uppercase(),
                        color = colors.gold,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        modifier = Modifier.padding(top = AurelvioSpacing.s),
                    )
                }

                when (val content = state.content) {
                    DashboardContent.Loading -> {
                        item { LoadingStateView() }
                    }
                    DashboardContent.Empty -> {
                        item {
                            EmptyStateView(
                                onAddFirstAssetClicked = { onAction(DashboardAction.AddEntryClicked) },
                            )
                        }
                    }
                    is DashboardContent.Error -> {
                        item {
                            ErrorStateView(
                                message = content.message,
                                onRetryClicked = { onAction(DashboardAction.Retry) },
                            )
                        }
                    }
                    is DashboardContent.Data -> {
                        val data = content.data

                        item {
                            HeroNetWorthCard(summary = data.summary)
                        }

                        item {
                            RangeSelector(
                                selectedRange = state.range,
                                onRangeSelected = { onAction(DashboardAction.RangeSelected(it)) },
                            )
                        }

                        item {
                            NetWorthChart(points = data.chartPoints)
                        }

                        item {
                            SummaryCardRow(summary = data.summary)
                        }

                        item {
                            TopAssetsSection(
                                assets = data.topAssets,
                                onSeeAllClicked = { onAction(DashboardAction.SeeAllAssetsClicked) },
                                onAssetClicked = { onAction(DashboardAction.AssetClicked(it)) },
                            )
                        }

                        item {
                            ActionButtonsRow(
                                onAddEntryClicked = { onAction(DashboardAction.AddEntryClicked) },
                                onUpdateValuesClicked = { onAction(DashboardAction.UpdateValuesClicked) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingStateView() {
    val colors = AurelvioTheme.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AurelvioSpacing.xl),
    ) {
        AurelvioCard(modifier = Modifier.fillMaxWidth().height(160.dp)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = colors.gold)
            }
        }
        AurelvioCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun EmptyStateView(
    onAddFirstAssetClicked: () -> Unit,
) {
    val colors = AurelvioTheme.colors

    AurelvioCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AurelvioSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.gold.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colors.gold, RoundedCornerShape(12.dp)),
                )
            }

            Spacer(modifier = Modifier.height(AurelvioSpacing.l))

            Text(
                text = stringResource(Res.string.dashboard_empty_title),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(AurelvioSpacing.s))

            Text(
                text = stringResource(Res.string.dashboard_empty_description),
                color = colors.mutedText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AurelvioSpacing.xl))

            Button(
                onClick = onAddFirstAssetClicked,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.gold,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.dashboard_empty_action),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetryClicked: () -> Unit,
) {
    val colors = AurelvioTheme.colors

    AurelvioCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AurelvioSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.dashboard_error_title),
                color = colors.negative,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(AurelvioSpacing.s))

            Text(
                text = message,
                color = colors.mutedText,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AurelvioSpacing.l))

            Button(
                onClick = onRetryClicked,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceElevated,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.dashboard_error_retry),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DashboardScreenDataPreview() {
    AurelvioTheme {
        DashboardScreen(
            state = DashboardState(
                range = TimeRange.ONE_MONTH,
                content = DashboardContent.Data(
                    data = DashboardData(
                        summary = NetWorthSummary(
                            currentValue = Money(285_430L),
                            periodChange = Money(45_430L),
                            valueGain = Money(42_930L),
                            deposits = Money(2_500L),
                            totalDeposits = Money(220_000L),
                            totalProfit = Money(65_430L),
                        ),
                        chartPoints = listOf(
                            NetWorthPoint(LocalDate(2025, 1, 22), Money(240_000L)),
                            NetWorthPoint(LocalDate(2025, 2, 22), Money(285_430L)),
                        ),
                        topAssets = listOf(
                            AssetItem(AssetId("1"), "ETF MSCI World", "Inwestycje", "#F59E0B", Money(97_350L), Money(11_350L)),
                            AssetItem(AssetId("2"), "Lokata 12M", "Konta i lokaty", "#10B981", Money(60_000L), Money(2_400L)),
                        ),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenLoadingPreview() {
    AurelvioTheme {
        DashboardScreen(
            state = DashboardState(content = DashboardContent.Loading),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenEmptyPreview() {
    AurelvioTheme {
        DashboardScreen(
            state = DashboardState(content = DashboardContent.Empty),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun DashboardScreenErrorPreview() {
    AurelvioTheme {
        DashboardScreen(
            state = DashboardState(content = DashboardContent.Error("Nie udało się połączyć z bazą danych.")),
            onAction = {},
        )
    }
}
