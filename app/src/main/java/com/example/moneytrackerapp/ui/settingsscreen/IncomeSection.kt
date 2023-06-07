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
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.formatSum

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
                    IncomeItem(income = it, currencyRate = currencyRate)
                }
            }
        }
    }
}

@Composable
fun IncomeItem(
    income: Income, currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.padding(8.dp)) {
        Text(
            text = income.yearMonthStr,
            fontSize = 16.sp,
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(modifier = modifier.weight(1F))
        Text(
            text = currencyRate.formatSum(income.sum),
            fontSize = 16.sp,
            style = MaterialTheme.typography.displayMedium
        )
    }
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
            AddIncomeDialogContent(uiState = uiState,
                onTextFieldValueChange = viewModel::updateIncomeSum,
                onSaveButtonClick = {
                    onHideDialog()
                    viewModel.saveIncome(currencyRate.rate)
                },
                onCancelButtonClick = onHideDialog,
                currency = currencyRate.currency
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@Composable
fun AddIncomeDialogContent(
    uiState: State<IncomeUIState>,
    onTextFieldValueChange: (String) -> Unit,
    onSaveButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit,
    currency: Currency,
    modifier: Modifier = Modifier
) {
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
            IncomeDialogTextField(
                uiState = uiState,
                currency = currency,
                onValueChange = onTextFieldValueChange
            )
            DialogButtons(
                onSaveClick = onSaveButtonClick,
                onCancelClick = onCancelButtonClick
            )

        }
    }
}

@Composable
fun IncomeDialogTextField(
    uiState: State<IncomeUIState>,
    currency: Currency,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.value.currentIncomeSum,
        onValueChange = onValueChange,
        isError = !uiState.value.isIncomeSumValid,
        label = {
            Text(
                text = "Income ($currency)",
                fontSize = 16.sp
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier.padding(top = 16.dp)
    )
}

@Composable
fun DialogButtons(
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 32.dp)
            .fillMaxWidth(0.7f)
    ) {
        Button(onClick = onCancelClick) {
            Text(text = "Cancel")
        }
        Spacer(modifier = modifier.weight(1F))
        Button(onClick = onSaveClick) {
            Text(text = "Save")
        }
    }
}