package com.example.moneytrackerapp.ui.expensescreen

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.NumberFormatException

data class ExpenseScreenUIState(
    val dropdownExpanded: Boolean = false,
    val name: String = "",
    val sum: String = "",
    val isSumValid: Boolean = true,
    val category: Category? = null,
    val note: String? = null
)

@HiltViewModel
class ExpenseScreenViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository,
    private val expenseRepo: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseScreenUIState())
    val uiState: StateFlow<ExpenseScreenUIState> = _uiState
    var categories: List<Category> = listOf()
        private set

    init {
        viewModelScope.launch {
            categoryRepo.getAllCategoriesFlow().collect { categories = it }
        }
    }

    fun updateExpenseName(newName: String) {
        _uiState.update {
            it.copy(name = newName)
        }
    }

    fun updateExpenseSum(sum: String) {
        val isSumValid = try {
            sum.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
        _uiState.update { it.copy(sum = sum, isSumValid = isSumValid) }
    }

    fun updateExpenseNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun updateExpenseCategory(category: Category) {
        _uiState.update { it.copy(category = category, dropdownExpanded = false) }
    }

    fun toggleDropdown() {
        val expanded = _uiState.value.dropdownExpanded
        _uiState.update { it.copy(dropdownExpanded = !expanded) }
    }

    fun hideDropdownOptions() {
        _uiState.update { it.copy(dropdownExpanded = false) }
    }

    fun saveExpense(rate: Double) {
        if (_uiState.value.isValid()) {
            viewModelScope.launch {
                expenseRepo.saveExpense(_uiState.value.toExpense(rate))
            }
        }
    }

}

private fun ExpenseScreenUIState.toExpense(rate: Double): Expense = Expense(
    name = this.name,
    date = System.currentTimeMillis(),
    sum = this.sum.toDouble() * rate,
    categoryId = this.category!!.id
)

@VisibleForTesting
internal fun ExpenseScreenUIState.isValid() = sum.isNotEmpty()
        && isSumValid && name.isNotEmpty() && category != null