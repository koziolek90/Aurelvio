package com.kris.aurelvio.feature.dashboard.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kris.aurelvio.core.designsystem.theme.AurelvioTheme
import com.kris.aurelvio.feature.dashboard.model.NetWorthPoint
import com.kris.aurelvio.feature.dashboard.ui.format.MoneyFormatter

@Composable
fun NetWorthChart(
    points: List<NetWorthPoint>,
    modifier: Modifier = Modifier
) {
    val colors = AurelvioTheme.colors
    val chartLineColor = colors.chartLine
    val fillTopColor = colors.chartFillTop
    val fillBottomColor = colors.chartFillBottom
    val mutedTextColor = colors.mutedText

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(points) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    val startDateText = points.firstOrNull()?.date?.toString() ?: ""
    val endDateText = points.lastOrNull()?.date?.toString() ?: ""
    val startAmount = points.firstOrNull()?.valueGrosze ?: 0L
    val endAmount = points.lastOrNull()?.valueGrosze ?: 0L

    val descriptionText = "Wykres majątku netto od ${MoneyFormatter.format(startAmount)} ($startDateText) do ${MoneyFormatter.format(endAmount)} ($endDateText)"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = descriptionText }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                if (points.isEmpty()) return@Canvas

                val width = size.width
                val height = size.height

                val values = points.map { it.valueGrosze }
                val minVal = values.minOrNull() ?: 0L
                val maxVal = values.maxOrNull() ?: 1L
                val rawDiff = (maxVal - minVal).coerceAtLeast(1L)

                val verticalPadding = height * 0.15f
                val availableHeight = height - (verticalPadding * 2)

                for (i in 0..2) {
                    val y = verticalPadding + (availableHeight / 2) * i
                    drawLine(
                        color = chartLineColor.copy(alpha = 0.1f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val currentProgress = animationProgress.value

                if (points.size == 1) {
                    val y = verticalPadding + availableHeight / 2
                    val center = Offset(width / 2, y)
                    drawCircle(chartLineColor, radius = 5.dp.toPx(), center = center)
                    return@Canvas
                }

                val stepX = width / (points.size - 1)
                val mappedPoints = points.mapIndexed { index, point ->
                    val normY = (point.valueGrosze - minVal).toFloat() / rawDiff.toFloat()
                    val x = index * stepX
                    val y = height - verticalPadding - (normY * availableHeight)
                    Offset(x, y)
                }

                val linePath = Path()
                val fillPath = Path()

                linePath.moveTo(mappedPoints[0].x, mappedPoints[0].y)
                fillPath.moveTo(mappedPoints[0].x, height)
                fillPath.lineTo(mappedPoints[0].x, mappedPoints[0].y)

                for (i in 0 until mappedPoints.size - 1) {
                    val p0 = if (i > 0) mappedPoints[i - 1] else mappedPoints[i]
                    val p1 = mappedPoints[i]
                    val p2 = mappedPoints[i + 1]
                    val p3 = if (i + 2 < mappedPoints.size) mappedPoints[i + 2] else p2

                    val ctrl1X = p1.x + (p2.x - p0.x) / 6f
                    val ctrl1Y = p1.y + (p2.y - p0.y) / 6f

                    val ctrl2X = p2.x - (p3.x - p1.x) / 6f
                    val ctrl2Y = p2.y - (p3.y - p1.y) / 6f

                    linePath.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, p2.x, p2.y)
                    fillPath.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, p2.x, p2.y)
                }

                val lastPoint = mappedPoints.last()
                fillPath.lineTo(lastPoint.x, height)
                fillPath.close()

                val animClipWidth = width * currentProgress

                drawContext.canvas.save()
                drawContext.canvas.clipRect(0f, 0f, animClipWidth, height)

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(fillTopColor, fillBottomColor),
                        startY = 0f,
                        endY = height
                    )
                )

                drawPath(
                    path = linePath,
                    color = chartLineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                if (currentProgress > 0.95f) {
                    drawCircle(
                        color = chartLineColor.copy(alpha = 0.3f),
                        radius = 10.dp.toPx(),
                        center = lastPoint
                    )
                    drawCircle(
                        color = chartLineColor,
                        radius = 5.dp.toPx(),
                        center = lastPoint
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = lastPoint
                    )
                }

                drawContext.canvas.restore()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = startDateText,
                color = mutedTextColor,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = endDateText,
                color = mutedTextColor,
                fontSize = 11.sp
            )
        }
    }
}
