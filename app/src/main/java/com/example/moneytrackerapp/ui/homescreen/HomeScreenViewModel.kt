package com.example.moneytrackerapp.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HomeScreenUIState(
    val dropdownExpanded: Boolean = false,
    val calendarOption: CalendarOption = CalendarOption.DAILY,
    val chosenDate: String = HomeScreenUtils.getCurrentDay(),
    val expenseSheetDisplayed: Boolean = false,
    val categoriesSheetDisplayed: Boolean = false
) {
    val chosenDateIdx: Int
        get() = calendarOption.datesList.indexOf(chosenDate)
}


data class HomeScreenDataState(
    val expenses: List<ExpenseTuple> = listOf()
)

class HomeScreenViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState
    lateinit var uiDataState: StateFlow<HomeScreenDataState>

    init {
        loadUIData()
    }

    private fun loadUIData() {
        val chosenDate = uiState.value.chosenDate
        val chosenDateRange = uiState.value.calendarOption.parseDateStr(chosenDate)
        uiDataState = expenseRepository.getExpensesByDate(chosenDateRange)
            .map { HomeScreenDataState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeScreenDataState()
            )
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
        println("new date: $newDate")
        _uiState.update { it.copy(chosenDate = newDate) }
        loadUIData()
    }

    fun updateChosenDate(localDate: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        _uiState.update {
            it.copy(
                chosenDate = localDate.format(formatter),
                calendarOption = CalendarOption.DAILY
            )
        }
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

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}