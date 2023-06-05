package com.example.moneytrackerapp.ui.homescreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrackerapp.data.entity.ExpenseTuple

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    inputValues: List<Double>
) {
    val chartDegrees = 360f
    var startAngle = 270f
    val proportions = inputValues.map {
        it * 100 / inputValues.sum()
    }
    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val canvasSize = Integer.min(constraints.maxWidth, constraints.maxHeight)
        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
        val canvasSizeDp = with(LocalDensity.current) { canvasSize.toDp() }
        Canvas(modifier = Modifier.size(canvasSizeDp)) {
            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle.toFloat(),
                    useCenter = true,
                    size = size,
                    style = Fill
                )
                startAngle += angle.toFloat()
            }

        }
    }
}

@Composable
fun PieChartHeader(
    categoryExpensesMap: Map<String, List<ExpenseTuple>>,
    chartColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column {
        categoryExpensesMap.keys.forEachIndexed { idx, category ->
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = modifier
                        .size(30.dp)
                        .background(chartColors[idx])
                )
                Text(
                    text = " - $category",
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 16.sp
                )
            }
        }
    }
}