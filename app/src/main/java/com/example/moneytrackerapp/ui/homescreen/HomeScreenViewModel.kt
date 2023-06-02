package com.example.moneytrackerapp.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.utils.CalendarOption
import com.example.moneytrackerapp.utils.DateUtils
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HomeScreenUIState(
    val dropdownExpanded: Boolean = false,
    val displayExpenses: List<ExpenseTuple> = listOf(),
    val calendarOption: CalendarOption = CalendarOption.DAILY,
    val chosenDate: String = DateUtils.getCurrentDay(),
    val expenseSheetDisplayed: Boolean = false,
    val categoriesSheetDisplayed: Boolean = false,
    val settingsSheetDisplayed: Boolean = false
) {
    val chosenDateIdx: Int
        get() = calendarOption.datesList.indexOf(chosenDate)

    val localDateTimeRange: Pair<LocalDateTime, LocalDateTime>
        get() = calendarOption.parseDateStr(chosenDate)
}


class HomeScreenViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState
    private var expenses: List<ExpenseTuple> = listOf()

    init {
        viewModelScope.launch {
            expenseRepository.getAllExpensesFlow()
                .collect {
                    expenses = it
                    updateUIStateExpenses()
                }
        }
    }

    private fun updateUIStateExpenses() {
        val chosenDate = uiState.value.chosenDate
        val dateRange = uiState.value.calendarOption.parseDateStr(chosenDate)
        val startDate = dateRange.first.toMillis()
        val endDate = dateRange.second.toMillis()
        val displayExpenses = expenses
            .filter { expense -> expense.date in startDate..endDate }
        _uiState.update {
            it.copy(displayExpenses = displayExpenses)
        }
    }

    fun changeDropdownOption(newIdx: Int) {
        val newCalendarOption = CalendarOption.values()[newIdx]
        _uiState.update {
            it.copy(
                calendarOption = newCalendarOption,
                dropdownExpanded = false,
                chosenDate = newCalendarOption.currentDate
            )
        }
    }

    fun expandDropdown() {
        _uiState.update { it.copy(dropdownExpanded = true) }
    }

    fun dismissDropdown() {
        _uiState.update { it.copy(dropdownExpanded = false) }
    }

    fun updateChosenDate(newDate: String) {
        _uiState.update { it.copy(chosenDate = newDate) }
        updateUIStateExpenses()
    }

    fun updateChosenDate(localDate: LocalDate) {
        _uiState.update {
            it.copy(
                chosenDate = localDate.format(DateUtils.DAY_FORMATTER),
                calendarOption = CalendarOption.DAILY
            )
        }
        updateUIStateExpenses()
    }

    fun displayExpenseSheet() {
        _uiState.update { it.copy(expenseSheetDisplayed = true) }
    }

    fun hideExpenseSheet() {
        _uiState.update { it.copy(expenseSheetDisplayed = false) }
    }

    fun displayCategoriesSheet() {
        _uiState.update { it.copy(categoriesSheetDisplayed = true) }
    }

    fun hideCategoriesSheet() {
        _uiState.update { it.copy(categoriesSheetDisplayed = false) }
    }

    fun displaySettingsSheet() {
        _uiState.update { it.copy(settingsSheetDisplayed = true) }
    }

    fun hideSettingsSheet() {
        _uiState.update { it.copy(settingsSheetDisplayed = false) }
    }

}