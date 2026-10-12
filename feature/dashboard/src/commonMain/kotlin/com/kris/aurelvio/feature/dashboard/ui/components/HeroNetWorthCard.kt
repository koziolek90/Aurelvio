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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.dashboard_deposits_label
import aurelvio.feature.dashboard.generated.resources.dashboard_net_worth_label
import aurelvio.feature.dashboard.generated.resources.dashboard_value_gain_label
import com.kris.aurelvio.core.designsystem.components.AurelvioCard
import com.kris.aurelvio.core.designsystem.components.TrendBadge
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.feature.dashboard.model.NetWorthSummary
import com.kris.aurelvio.feature.dashboard.ui.format.MoneyFormatter
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeroNetWorthCard(
    summary: NetWorthSummary,
    modifier: Modifier = Modifier
) {
    val colors = AurelvioTheme.colors

    AurelvioCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AurelvioSpacing.xl)) {
            Text(
                text = stringResource(Res.string.dashboard_net_worth_label),
                color = colors.mutedText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(AurelvioSpacing.xs))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MoneyFormatter.format(summary.currentValue),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TrendBadge(
                    text = MoneyFormatter.format(summary.periodChange, includeSign = true),
                    isPositive = summary.periodChange.isPositive || summary.periodChange.isZero
                )
            }

            Spacer(modifier = Modifier.height(AurelvioSpacing.m))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.dashboard_value_gain_label),
                    color = colors.mutedText,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(AurelvioSpacing.xs))
                Text(
                    text = MoneyFormatter.format(summary.valueGain, includeSign = true),
                    color = if (summary.valueGain.isPositive || summary.valueGain.isZero) colors.positive else colors.negative,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(AurelvioSpacing.m))

                Text(
                    text = stringResource(Res.string.dashboard_deposits_label),
                    color = colors.mutedText,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(AurelvioSpacing.xs))
                Text(
                    text = MoneyFormatter.format(summary.deposits, includeSign = true),
                    color = colors.goldSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
