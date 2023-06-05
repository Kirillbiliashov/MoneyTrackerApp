package com.example.moneytrackerapp.ui.settingsscreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.entity.localDateRangeString
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.formatSum
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

@Composable
fun SettingsSheetContent(
    viewModel: SettingsScreenViewModel,
    onSaveFileClick: () -> Unit,
    onButtonClick: () -> Unit, modifier: Modifier = Modifier,
    currencyRate: CurrencyRate,
    onUpdateCurrency: (Currency) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    var limitDialogDisplayed by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (limitDialogDisplayed) {
            AddLimitDialog(
                viewModel = viewModel,
                uiState = uiState,
                onHideDialog = { limitDialogDisplayed = false },
                currencyRate = currencyRate
            )
        }
        LimitSection(viewModel = viewModel,
            currencyRate = currencyRate,
            onAddLimit = { limitDialogDisplayed = true })
        CurrenciesSection(
            onRadioButtonClick = { onUpdateCurrency(it) },
            onArrowIconClick = viewModel::toggleDisplayCurrencies,
            currencyRate = currencyRate,
            currenciesDisplayed = uiState.value.currenciesDisplayed
        )
        Text(
            text = "Save expenses to a file",
            textDecoration = TextDecoration.Underline,
            fontSize = 16.sp,
            modifier = modifier.clickable(onClick = onSaveFileClick)
        )
    }
}

@Composable
fun CurrenciesSection(onRadioButtonClick: (Currency) -> Unit,
                      onArrowIconClick: () -> Unit,
                      currencyRate: CurrencyRate,
                      currenciesDisplayed: Boolean,
    modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Currency", style = MaterialTheme.typography.displayMedium)
        val icon = if (currenciesDisplayed) Icons.Default.KeyboardArrowUp
        else Icons.Default.KeyboardArrowDown
        IconButton(onClick = onArrowIconClick) {
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
                        onClick = { onRadioButtonClick(it)})
                    Text(text = it.toString())
                }
            }
        }
    }
}


@Composable
fun AddLimitDialog(
    viewModel: SettingsScreenViewModel,
    uiState: State<SettingsScreenUIState>,
    onHideDialog: () -> Unit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberSheetState(
        onCloseRequest = {
            if (uiState.value.chosenDates.isNotEmpty()) {
                viewModel.saveLimit(currencyRate.rate)
            }
            onHideDialog()
        })
    Dialog(
        onDismissRequest = onHideDialog,
        content = {
            AddLimitDialogContent(
                viewModel = viewModel,
                uiState = uiState,
                currency = currencyRate.currency,
                sheetState = sheetState
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLimitDialogContent(
    viewModel: SettingsScreenViewModel,
    uiState: State<SettingsScreenUIState>,
    currency: Currency,
    sheetState: com.maxkeppeker.sheets.core.models.base.SheetState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Limit",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.W500
            )
            Divider(thickness = 2.dp)
            LimitSumTextField(
                currency = currency,
                uiState = uiState,
                onValueChange = viewModel::updateLimitSum
            )
            CalendarView(
                sheetState = sheetState,
                selection = CalendarSelection.Dates { viewModel.updateChosenDates(it) },
                config = CalendarConfig(
                    disabledDates = listOf(LocalDate.now()),
                    disabledTimeline = CalendarTimeline.PAST
                )
            )
        }
    }
}


@Composable
fun LimitSumTextField(
    currency: Currency,
    uiState: State<SettingsScreenUIState>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.value.currentLimitSum.toString(),
        onValueChange = onValueChange,
        label = { Text(text = "Sum ($currency)", fontSize = 16.sp) },
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier.padding(top = 16.dp)
    )
}

@Composable
fun LimitSection(
    viewModel: SettingsScreenViewModel,
    currencyRate: CurrencyRate,
    onAddLimit: () -> Unit, modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val limits = viewModel.limits.collectAsState()
    val limitsDisplayed = uiState.value.limitsDisplayed
    val icon = if (limitsDisplayed) Icons.Default.KeyboardArrowUp
    else Icons.Default.KeyboardArrowDown
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Limits", style = MaterialTheme.typography.displayMedium)
        IconButton(onClick = viewModel::toggleDisplayLimits) {
            Icon(imageVector = icon, contentDescription = null)
        }
        Spacer(modifier = modifier.weight(1F))
        Button(onClick = onAddLimit) {
            Text(text = "Add limit")
        }
    }
    if (uiState.value.limitsDisplayed) {
        LazyColumn() {
            items(limits.value) {
                LimitItem(limit = it, currencyRate = currencyRate)
            }
        }
    }
}

@Composable
fun LimitItem(
    limit: Limit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Text(
            text = limit.localDateRangeString(),
            fontSize = 16.sp,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = modifier.weight(1F))
        Text(
            text = currencyRate.formatSum(limit.sum),
            fontSize = 16.sp,
            style = MaterialTheme.typography.displayMedium
        )
    }
}