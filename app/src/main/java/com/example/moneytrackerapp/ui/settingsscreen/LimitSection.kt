package com.example.moneytrackerapp.ui.settingsscreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.entity.localDateRangeString
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.formatSum
import com.maxkeppeker.sheets.core.models.base.SheetState
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import java.time.LocalDate


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
            uiState = uiState,
            onHideDialog = viewModel::hideLimitDialog,
            currencyRate = currencyRate,
            onSaveLimit = { viewModel.saveLimit(currencyRate.rate, it) },
            onUpdateLimitSum = viewModel::updateLimitSum
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

@Composable
fun AddLimitDialog(
    uiState: State<LimitUIState>,
    onSaveLimit: (LocalDate) -> Unit,
    onHideDialog: () -> Unit,
    currencyRate: CurrencyRate,
    onUpdateLimitSum: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberSheetState(
        onFinishedRequest = { onHideDialog() },
    )
    Dialog(
        onDismissRequest = onHideDialog,
        content = {
            AddLimitDialogContent(
                uiState = uiState,
                currency = currencyRate.currency,
                sheetState = sheetState,
                onSaveLimit = onSaveLimit,
                onUpdateLimitSum = onUpdateLimitSum
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
    uiState: State<LimitUIState>,
    currency: Currency,
    sheetState: SheetState,
    onSaveLimit: (LocalDate) -> Unit,
    onUpdateLimitSum: (String) -> Unit,
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
                onValueChange = onUpdateLimitSum
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