package com.example.moneytrackerapp.ui.expensescreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ExpenseScreenUIState(
    val dropdownExpanded: Boolean = false,
    val name: String = "",
    val sum: Double = 0.0,
    val category: String? = null,
    val note: String? = null
)

class ExpenseScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseScreenUIState())
    val uiState: StateFlow<ExpenseScreenUIState> = _uiState

    fun updateExpenseName(newName: String) {
        _uiState.update {
            it.copy(name = newName)
        }
    }

    fun updateExpenseSum(sum: String) {
        _uiState.update { it.copy(sum = sum.toDouble()) }
    }

    fun updateExpenseNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun updateExpenseCategory(category: String) {
        _uiState.update { it.copy(category = category, dropdownExpanded = false) }
    }

    fun toggleDropdown() {
        val expanded = _uiState.value.dropdownExpanded
        _uiState.update { it.copy(dropdownExpanded = !expanded) }
    }

    fun hideDropdownOptions() {
        _uiState.update { it.copy(dropdownExpanded = false) }
    }

}