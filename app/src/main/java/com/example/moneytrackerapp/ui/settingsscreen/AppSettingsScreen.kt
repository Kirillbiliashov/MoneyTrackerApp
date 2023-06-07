package com.example.moneytrackerapp.ui.settingsscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.entity.localDateRangeString
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.formatSum
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

@Composable
fun SettingsSheetContent(
    viewModel: SettingsScreenViewModel,
    onSaveFileClick: () -> Unit,
    onButtonClick: () -> Unit, modifier: Modifier = Modifier,
    currencyRate: CurrencyRate,
    onUpdateCurrency: (Currency) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LimitSection(
            viewModel = viewModel,
            currencyRate = currencyRate
        )
        IncomeSection(
            viewModel = viewModel,
            currencyRate = currencyRate
        )
        CurrenciesSection(
            onRadioButtonClick = { onUpdateCurrency(it) },
            currencyRate = currencyRate
        )
        SaveFileText(onTextClick = onSaveFileClick)
        Spacer(modifier = modifier.weight(1f))
        Button(onClick = onButtonClick,
            modifier = modifier.padding(bottom = 64.dp)) {
            Text(text = "Ok")
        }
    }
}

@Composable
fun SaveFileText(onTextClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = "Save expenses to a file",
        textDecoration = TextDecoration.Underline,
        fontSize = 16.sp,
        modifier = modifier
            .clickable(onClick = onTextClick)
    )
}

@Composable
fun CurrenciesSection(
    onRadioButtonClick: (Currency) -> Unit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    var currenciesDisplayed by remember { mutableStateOf(false) }
    Column(modifier = modifier.animateContentSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Currency", style = MaterialTheme.typography.displayMedium)
            val icon = if (currenciesDisplayed) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown
            IconButton(onClick = { currenciesDisplayed = !currenciesDisplayed }) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Spacer(modifier = modifier.weight(1f))
            Text(
                text = currencyRate.currency.toString(),
                style = MaterialTheme.typography.displayMedium
            )
        }
        if (currenciesDisplayed) {
            Column(
                modifier = modifier
                    .selectableGroup()
                    .fillMaxWidth()
            ) {
                Currency.values().forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = currencyRate.currency == it,
                            onClick = { onRadioButtonClick(it) })
                        Text(text = it.toString())
                    }
                }
            }
        }
    }
}
