package com.example.moneytrackerapp.ui.expensescreen

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.ui.ViewModelProvider

@Composable
fun ExpenseSheetContent(onSaveClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddExpenseForm(onSaveClick = onSaveClick)
    }
}

@Composable
fun AddExpenseForm(onSaveClick: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: ExpenseScreenViewModel = viewModel(factory = ViewModelProvider.Factory)
    val uiState = viewModel.uiState.collectAsState()
    Text(text = "Add expense", fontSize = 32.sp, modifier = modifier.padding(vertical = 16.dp))
    OutlinedTextField(
        value = uiState.value.name,
        onValueChange = viewModel::updateExpenseName,
        label = { Text(text = "Expense name") },
        modifier = modifier.padding(vertical = 8.dp)
    )
    OutlinedTextField(
        value = uiState.value.sum.toString(),
        onValueChange = viewModel::updateExpenseSum,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(text = "Sum") },
        modifier = modifier.padding(vertical = 8.dp)
    )
    CategoryDropdown(viewModel = viewModel)
    OutlinedTextField(
        value = uiState.value.note ?: "",
        onValueChange = viewModel::updateExpenseNote,
        label = { Text(text = "Note") },
        modifier = modifier.padding(vertical = 8.dp)
    )
    Button(onClick = {
        viewModel.saveExpense()
        onSaveClick()
    }, modifier = modifier.padding(top = 8.dp)) {
        Text(text = "Save")
    }
}

@Composable
fun CategoryDropdown(
    viewModel: ExpenseScreenViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val expanded = uiState.value.dropdownExpanded
    Box {
        DropdownTextField(
            expanded = expanded,
            category = uiState.value.category,
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
    expanded: Boolean, category: Category?, onIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageVector = if (expanded) Icons.Default.KeyboardArrowUp
    else Icons.Default.KeyboardArrowDown
    OutlinedTextField(
        value = category?.name ?: "Select category",
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
        label = { Text(text = "Category") },
        modifier = modifier.padding(vertical = 8.dp)
    )
}