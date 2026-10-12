package com.kris.aurelvio.feature.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.dashboard_range_1m
import aurelvio.feature.dashboard.generated.resources.dashboard_range_1y
import aurelvio.feature.dashboard.generated.resources.dashboard_range_6m
import aurelvio.feature.dashboard.generated.resources.dashboard_range_all
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.feature.dashboard.model.TimeRange
import org.jetbrains.compose.resources.stringResource

@Composable
fun RangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AurelvioTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.surfaceElevated,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(AurelvioSpacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TimeRange.entries.forEach { range ->
            val isSelected = range == selectedRange
            val label = when (range) {
                TimeRange.ONE_MONTH -> stringResource(Res.string.dashboard_range_1m)
                TimeRange.SIX_MONTHS -> stringResource(Res.string.dashboard_range_6m)
                TimeRange.ONE_YEAR -> stringResource(Res.string.dashboard_range_1y)
                TimeRange.ALL -> stringResource(Res.string.dashboard_range_all)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) colors.gold else Color.Transparent,
                    )
                    .clickable { onRangeSelected(range) }
                    .padding(vertical = AurelvioSpacing.s),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else colors.mutedText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}
