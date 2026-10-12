package com.kris.aurelvio.feature.dashboard.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.dashboard_total_deposits_label
import aurelvio.feature.dashboard.generated.resources.dashboard_total_profit_label
import com.kris.aurelvio.core.designsystem.components.AurelvioCard
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.feature.dashboard.model.NetWorthSummary
import com.kris.aurelvio.feature.dashboard.ui.format.MoneyFormatter
import org.jetbrains.compose.resources.stringResource

@Composable
fun SummaryCardRow(
    summary: NetWorthSummary,
    modifier: Modifier = Modifier
) {
    val colors = AurelvioTheme.colors

    Row(modifier = modifier.fillMaxWidth()) {
        AurelvioCard(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(AurelvioSpacing.l)) {
                Text(
                    text = stringResource(Res.string.dashboard_total_deposits_label),
                    color = colors.mutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(AurelvioSpacing.xs))
                Text(
                    text = MoneyFormatter.format(summary.totalDepositsGrosze),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(AurelvioSpacing.m))

        AurelvioCard(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.padding(AurelvioSpacing.l)) {
                Text(
                    text = stringResource(Res.string.dashboard_total_profit_label),
                    color = colors.mutedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(AurelvioSpacing.xs))
                Text(
                    text = MoneyFormatter.format(summary.totalProfitGrosze, includeSign = true),
                    color = if (summary.totalProfitGrosze >= 0) colors.positive else colors.negative,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
