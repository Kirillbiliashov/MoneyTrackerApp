package com.example.moneytrackerapp.ui.homescreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.formatSum

@Composable
fun BarChart(data: Map<Double, String>,
             currencyRate: CurrencyRate,
             maxValue: Double, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            BarChartVerticalScale(
                currencyRate = currencyRate,
                maxValue = maxValue)
            BarChartBars(data = data, maxValue = maxValue)
        }
        BarChartHorizontalScale(items = data.values.toList())
    }
}

@Composable
fun BarChartBars(data: Map<Double, String>,
                 maxValue: Double,
                 modifier: Modifier = Modifier
) {
    val ctxt = LocalContext.current
    data.forEach {
        Box(
            modifier = modifier
                .padding(start = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .width(30.dp)
                .fillMaxHeight((it.key / maxValue).toFloat())
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    Toast
                        .makeText(ctxt, it.key.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        )
    }
}

@Composable
fun BarChartVerticalScale(
    currencyRate: CurrencyRate,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(65.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        var i = 1f
        while (i >= 0.25) {
            Column(
                modifier = modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(text = currencyRate.formatSum(maxValue * i))
                Spacer(modifier = Modifier.fillMaxHeight(i))
            }
            i -= 0.25f
        }
    }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(2.dp)
            .background(Color.Black)
    )
}

@Composable
fun BarChartHorizontalScale(items: List<String>,
                            modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.Black)
    )
    Row(
        modifier = modifier
            .padding(start = 87.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(40.dp),
                fontSize = 14.sp
            )
        }
    }
}