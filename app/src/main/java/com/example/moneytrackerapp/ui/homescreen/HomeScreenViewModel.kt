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
    val calendarTypeIdx: Int = 0,
    val chosenDate: String = HomeScreenUtils.getCurrentDay(),
    val expenseSheetDisplayed: Boolean = false,
    val categoriesSheetDisplayed: Boolean = false
)

data class HomeScreenDataState(
    val expenses: List<ExpenseTuple> = listOf()
)

class HomeScreenViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState
    val uiDataState: StateFlow<HomeScreenDataState>
    val calendarOptions = listOf("Daily", "Monthly", "Weekly")
    val currentCalendarOption: String
        get() = calendarOptions[_uiState.value.calendarTypeIdx]

    val chosenDateIdx: Int
        get() = dateItems.indexOf(uiState.value.chosenDate)

    val dateItems: List<String>
        get() = when (currentCalendarOption) {
            "Monthly" -> HomeScreenUtils.getMonthsList()
            "Weekly" -> HomeScreenUtils.getWeeksList()
            else -> HomeScreenUtils.getDaysList()
        }

    init {
        uiDataState = expenseRepository.getExpensesByDate(LocalDate.now())
            .map {
                println("size of result is ${it.size}")
                HomeScreenDataState(it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeScreenDataState()
            )
    }

    fun changeDropdownOption(newIdx: Int) {
        _uiState.update {
            it.copy(
                calendarTypeIdx = newIdx,
                dropdownExpanded = false,
                chosenDate = when (newIdx) {
                    1 -> HomeScreenUtils.getCurrentMonth()
                    2 -> HomeScreenUtils.getCurrentWeek()
                    else -> HomeScreenUtils.getCurrentDay()
                }
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
    }

    fun updateChosenDate(localDate: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        _uiState.update {
            it.copy(
                chosenDate = localDate.format(formatter),
                calendarTypeIdx = 0
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