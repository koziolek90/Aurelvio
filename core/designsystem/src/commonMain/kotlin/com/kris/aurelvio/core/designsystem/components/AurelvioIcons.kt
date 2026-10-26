package com.kris.aurelvio.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun BackArrowIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground,
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 2.dp.toPx()
        val width = size.width
        val height = size.height
        val centerY = height / 2f

        drawLine(
            color = tint,
            start = Offset(width * 0.2f, centerY),
            end = Offset(width * 0.8f, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(width * 0.2f, centerY),
            end = Offset(width * 0.45f, centerY - height * 0.25f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = tint,
            start = Offset(width * 0.2f, centerY),
            end = Offset(width * 0.45f, centerY + height * 0.25f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
fun CheckIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val strokeWidth = 2.5.dp.toPx()
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width * 0.2f, height * 0.5f)
            lineTo(width * 0.42f, height * 0.72f)
            lineTo(width * 0.8f, height * 0.28f)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
