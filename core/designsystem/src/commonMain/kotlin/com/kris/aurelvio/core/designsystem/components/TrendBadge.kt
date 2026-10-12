package com.kris.aurelvio.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kris.aurelvio.core.designsystem.theme.AurelvioSpacing
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme

@Composable
fun TrendBadge(
    text: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    isNeutral: Boolean = false
) {
    val colors = AurelvioTheme.colors
    val badgeColor = when {
        isNeutral -> colors.mutedText
        isPositive -> colors.positive
        else -> colors.negative
    }
    val arrowSymbol = when {
        isNeutral -> "−"
        isPositive -> "↑"
        else -> "↓"
    }

    Row(
        modifier = modifier
            .background(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = AurelvioSpacing.s, vertical = AurelvioSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arrowSymbol,
            color = badgeColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(AurelvioSpacing.xxs))
        Text(
            text = text,
            color = badgeColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
