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
        Text(
            text = "Save expenses to a file",
            textDecoration = TextDecoration.Underline,
            fontSize = 16.sp,
            modifier = modifier
                .clickable(onClick = onSaveFileClick)
        )
        Spacer(modifier = modifier.weight(1f))
        Button(onClick = onButtonClick,
            modifier = modifier.padding(bottom = 64.dp)) {
            Text(text = "Ok")
        }
    }
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

@Composable
fun IncomeSection(
    viewModel: SettingsScreenViewModel,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.incomeUIState.collectAsState()
    val incomeHistory = viewModel.incomeHistory.collectAsState()
    val incomeDialogDisplayed = uiState.value.incomeDialogDisplayed
    val incomeHistoryDisplayed = uiState.value.incomeHistoryDisplayed
    if (incomeDialogDisplayed) {
        AddIncomeDialog(
            viewModel = viewModel,
            uiState = uiState,
            currencyRate = currencyRate,
            onHideDialog = viewModel::hideIncomeDialog
        )
    }
    Column(modifier = modifier.animateContentSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Income history", style = MaterialTheme.typography.displayMedium)
            val icon = if (incomeHistoryDisplayed) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown
            IconButton(onClick = viewModel::toggleIncomeHistory) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Spacer(modifier = modifier.weight(1f))
            Button(onClick = viewModel::showIncomeDialog) {
                Text(text = "Add income")
            }
        }
        if (incomeHistoryDisplayed) {
            LazyColumn {
                items(items = incomeHistory.value) {
                    Row(modifier = modifier.padding(8.dp)) {
                        Text(
                            text = it.yearMonthStr,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = modifier.weight(1F))
                        Text(
                            text = currencyRate.formatSum(it.sum),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AddLimitDialog(
    uiState: State<LimitUIState>,
    viewModel: SettingsScreenViewModel,
    onHideDialog: () -> Unit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberSheetState(
        onFinishedRequest = { onHideDialog() },
    )
    Dialog(
        onDismissRequest = onHideDialog,
        content = {
            AddLimitDialogContent(
                viewModel = viewModel,
                uiState = uiState,
                currency = currencyRate.currency,
                sheetState = sheetState,
                onSaveLimit = { viewModel.saveLimit(currencyRate.rate, it) }
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@Composable
fun AddIncomeDialog(
    onHideDialog: () -> Unit,
    viewModel: SettingsScreenViewModel,
    uiState: State<IncomeUIState>,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onHideDialog,
        content = {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5F)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "Add income",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.W600
                    )
                    Divider(thickness = 0.5.dp, color = Color.Black)
                    OutlinedTextField(
                        value = uiState.value.currentIncomeSum,
                        onValueChange = viewModel::updateIncomeSum,
                        isError = !uiState.value.isIncomeSumValid,
                        label = {
                            Text(
                                text = "Income (${currencyRate.currency})",
                                fontSize = 16.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = modifier.padding(top = 16.dp)
                    )
                    Row(
                        modifier = modifier
                            .padding(top = 32.dp)
                            .fillMaxWidth(0.7f)
                    ) {
                        Button(onClick = onHideDialog) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = modifier.weight(1F))
                        Button(onClick = {
                            onHideDialog()
                            viewModel.saveIncome(currencyRate.rate)
                        }) {
                            Text(text = "Save")
                        }
                    }
                }
            }
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
    uiState: State<LimitUIState>,
    currency: Currency,
    sheetState: com.maxkeppeker.sheets.core.models.base.SheetState,
    onSaveLimit: (LocalDate) -> Unit,
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
                fontWeight = FontWeight.W600
            )
            Divider(thickness = 0.5.dp, color = Color.Black)
            LimitSumTextField(
                currency = currency,
                uiState = uiState,
                onValueChange = viewModel::updateLimitSum
            )
            CalendarView(
                sheetState = sheetState,
                selection = CalendarSelection.Date { onSaveLimit(it) },
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
    uiState: State<LimitUIState>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        isError = !uiState.value.isLimitSumValid,
        value = uiState.value.currentLimitSum,
        onValueChange = onValueChange,
        label = { Text(text = "Sum ($currency)", fontSize = 16.sp) },
        keyboardOptions = KeyboardOptions(
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
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.limitUIState.collectAsState()
    val limitDialogDisplayed = uiState.value.limitDialogDisplayed
    val limits = viewModel.limits.collectAsState()
    val limitsDisplayed = uiState.value.limitsDisplayed
    val icon = if (limitsDisplayed) Icons.Default.KeyboardArrowUp
    else Icons.Default.KeyboardArrowDown
    if (limitDialogDisplayed) {
        AddLimitDialog(
            viewModel = viewModel,
            uiState = uiState,
            onHideDialog = viewModel::hideLimitDialog,
            currencyRate = currencyRate
        )
    }
    Column(modifier = modifier.animateContentSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Limits", style = MaterialTheme.typography.displayMedium)
            IconButton(onClick = viewModel::toggleDisplayLimits) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Spacer(modifier = modifier.weight(1F))
            Button(onClick = viewModel::showLimitDialog) {
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