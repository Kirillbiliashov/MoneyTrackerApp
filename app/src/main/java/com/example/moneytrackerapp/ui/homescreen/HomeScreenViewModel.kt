package com.example.moneytrackerapp.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.repo.CurrencyRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.data.repo.SaveFileRepository
import com.example.moneytrackerapp.data.repo.UserCurrencyRepository
import com.example.moneytrackerapp.utils.CalendarOption
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.DateUtils
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class HomeScreenUIState(
    val dropdownExpanded: Boolean = false,
    val displayExpenses: List<ExpenseTuple> = listOf(),
    val calendarOption: CalendarOption = CalendarOption.DAILY,
    val chosenDate: String = DateUtils.getCurrentDay(),
    val currentCurrencyRate: CurrencyRate = CurrencyRate(),
    val expenseSheetDisplayed: Boolean = false,
    val categoriesSheetDisplayed: Boolean = false,
    val settingsSheetDisplayed: Boolean = false,
    val expenseStatsDisplayed: Boolean = false
) {
    val chosenDateIdx: Int
        get() = calendarOption.datesList.indexOf(chosenDate)

    val localDateTimeRange: Pair<LocalDateTime, LocalDateTime>
        get() = calendarOption.parseDateStr(chosenDate)
}


class HomeScreenViewModel(
    private val expenseRepository: ExpenseRepository,
    private val saveFileRepository: SaveFileRepository,
    private val currencyRepository: CurrencyRepository,
    private val userCurrencyRepository: UserCurrencyRepository
) : ViewModel() {

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
        viewModelScope.launch {
            userCurrencyRepository.currency.collect {
                updateChosenCurrency(newCurrency = Currency.valueOf(it))
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

    fun saveExpensesToFile() {
        saveFileRepository.saveExpensesToFile(expenses)
    }

    fun toggleExpenseDisplayStyle() {
        val expenseStatsDisplayed = _uiState.value.expenseStatsDisplayed
        _uiState.update { it.copy(expenseStatsDisplayed = !expenseStatsDisplayed) }
    }

    fun updateChosenCurrency(newCurrency: Currency) {
        viewModelScope.launch {
            val json = currencyRepository.getCurrencyRates(newCurrency)
            val rate = updateRate(json)
            _uiState.update {
                it.copy(
                    currentCurrencyRate = CurrencyRate(
                        currency = newCurrency,
                        rate = rate
                    )
                )
            }
        }
        viewModelScope.launch {
            println("saving to preferences...")
            userCurrencyRepository.saveCurrency(newCurrency.toString())
        }
    }

    private fun updateRate(jsonString: String): Double {
        val lines = jsonString.split(",\n")
        val usdString = lines.findLast { it.contains("\"usd\"") }
        return if (usdString != null) {
            val keyValue = usdString.split(":")
            keyValue[1].trim().toDouble()
        } else {
            throw IllegalStateException("Unable to parse currency json data")
        }
    }

}