package com.example.moneytrackerapp.ui.homescreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HomeScreenUIState(
    val dropdownExpanded: Boolean = false,
    val calendarTypeIdx: Int = 0,
    val chosenDate: String = HomeScreenUtils.getCurrentDate()
)

class HomeScreenViewModel : ViewModel() {
    private var _uiState: MutableState<HomeScreenUIState> =
        mutableStateOf(HomeScreenUIState())
    val uiState: State<HomeScreenUIState> = _uiState
    val calendarOptions = listOf("Daily", "Monthly", "Weekly")
    val currentCalendarOption: String
        get() = calendarOptions[_uiState.value.calendarTypeIdx]
    val dateItems: List<String>
        get() = when (currentCalendarOption) {
            "Monthly" -> HomeScreenUtils.getMonthsList()
            "Weekly" -> HomeScreenUtils.getWeeksList()
            else -> HomeScreenUtils.getDaysList()
        }

    fun changeDropdownOption(newIdx: Int) {
        _uiState.value = _uiState.value.copy(
            calendarTypeIdx = newIdx,
            dropdownExpanded = false
        )
    }

    fun expandDropdown() {
        _uiState.value = _uiState.value.copy(dropdownExpanded = true)
    }

    fun dismissDropdown() {
        _uiState.value = _uiState.value.copy(dropdownExpanded = false)
    }

    fun updateChosenDate(newDate: String) {
        _uiState.value = _uiState.value.copy(chosenDate = newDate)
    }

    fun updateChosenDate(localDate: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        _uiState.value = _uiState.value.copy(
            chosenDate = localDate.format(formatter),
            calendarTypeIdx = 0
        )
    }


}