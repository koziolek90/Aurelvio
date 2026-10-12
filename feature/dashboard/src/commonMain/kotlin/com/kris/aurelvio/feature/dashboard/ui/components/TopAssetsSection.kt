package com.kris.aurelvio.feature.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import aurelvio.feature.dashboard.generated.resources.Res
import aurelvio.feature.dashboard.generated.resources.dashboard_see_all_assets
import aurelvio.feature.dashboard.generated.resources.dashboard_top_assets_title
import com.kris.aurelvio.core.designsystem.components.AurelvioCard
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.feature.dashboard.model.AssetItem
import com.kris.aurelvio.feature.dashboard.ui.format.MoneyFormatter
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopAssetsSection(
    assets: List<AssetItem>,
    onSeeAllClicked: () -> Unit,
    onAssetClicked: (AssetId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AurelvioTheme.colors

    AurelvioCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(AurelvioSpacing.l)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.dashboard_top_assets_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onSeeAllClicked) {
                    Text(
                        text = stringResource(Res.string.dashboard_see_all_assets),
                        color = colors.gold,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(AurelvioSpacing.xs))

            assets.forEachIndexed { index, asset ->
                AssetRowItem(
                    asset = asset,
                    onClick = { onAssetClicked(asset.id) },
                )
                if (index < (assets.size - 1)) {
                    HorizontalDivider(
                        color = colors.surfaceElevated,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = AurelvioSpacing.xs),
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetRowItem(
    asset: AssetItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AurelvioTheme.colors
    val fallbackColor = colors.gold
    val dotColor = remember(asset.colorHex, fallbackColor) {
        parseHexColor(asset.colorHex, fallback = fallbackColor)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AurelvioSpacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = dotColor, shape = CircleShape),
        )

        Spacer(modifier = Modifier.width(AurelvioSpacing.m))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = asset.name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = asset.category,
                color = colors.mutedText,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = MoneyFormatter.format(asset.value),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = MoneyFormatter.format(asset.profit, includeSign = true),
                color = if (asset.profit.isPositive || asset.profit.isZero) colors.positive else colors.negative,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            Color(colorInt or 0xFF000000L)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        fallback
    }
}
