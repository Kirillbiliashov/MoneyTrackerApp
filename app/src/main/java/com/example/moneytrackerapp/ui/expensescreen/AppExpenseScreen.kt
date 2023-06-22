package com.example.moneytrackerapp.ui.expensescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.R
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.utils.CurrencyRate

@Composable
fun ExpenseSheetContent(
    onSaveClick: () -> Unit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddExpenseForm(onSaveClick = onSaveClick, currencyRate = currencyRate)
    }
}

@Composable
fun AddExpenseForm(
    onSaveClick: () -> Unit,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val viewModel: ExpenseScreenViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState()
    Text(
        text = stringResource(R.string.add_expense), fontSize = 32.sp,
        modifier = modifier.padding(vertical = 16.dp)
    )
    ExpenseTextField(
        value = uiState.value.name,
        onValueChange = viewModel::updateExpenseName,
        text = stringResource(R.string.expense_name)
    )
    ExpenseTextField(
        value = uiState.value.sum,
        onValueChange = viewModel::updateExpenseSum,
        text = stringResource(R.string.sum, currencyRate.currency),
        isError = !uiState.value.isSumValid,
        isNumericKeyboard = true
    )
    CategoryDropdown(expanded = uiState.value.dropdownExpanded,
        category = uiState.value.category,
        viewModel = viewModel)
    ExpenseTextField(
        value = uiState.value.note ?: "",
        onValueChange = viewModel::updateExpenseNote,
        text = stringResource(R.string.note)
    )
    Button(onClick = {
        viewModel.saveExpense(currencyRate.rate)
        onSaveClick()
    }, modifier = modifier.padding(top = 8.dp)) {
        Text(text = stringResource(R.string.save))
    }
}

@Composable
fun ExpenseTextField(
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isNumericKeyboard: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    text: String
) {
    OutlinedTextField(
        isError = isError,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = if (isNumericKeyboard)
            KeyboardOptions(keyboardType = KeyboardType.Number)
        else KeyboardOptions.Default,
        label = { Text(text = text) },
        modifier = modifier.padding(vertical = 8.dp)
    )
}


@Composable
fun CategoryDropdown(
    expanded: Boolean,
    category: Category?,
    viewModel: ExpenseScreenViewModel,
    modifier: Modifier = Modifier
) {
    Box {
        DropdownTextField(
            expanded = expanded,
            category = category,
            onIconClick = viewModel::toggleDropdown
        )
        DropdownMenu(
            offset = DpOffset(0.dp, 65.dp),
            expanded = expanded,
            onDismissRequest = viewModel::hideDropdownOptions,
            modifier = modifier.fillMaxWidth(0.68f)
        ) {
            viewModel.categories.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = { viewModel.updateExpenseCategory(it) }
                )
            }
        }
    }
}

@Composable
fun DropdownTextField(
    expanded: Boolean,
    category: Category?,
    onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageVector = if (expanded) Icons.Default.KeyboardArrowUp
    else Icons.Default.KeyboardArrowDown
    OutlinedTextField(
        value = category?.name ?: stringResource(R.string.select_category),
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = modifier.clickable(onClick = onIconClick)
            )
        },
        onValueChange = {},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = stringResource(R.string.category_text)) },
        modifier = modifier.padding(vertical = 8.dp)
    )
}