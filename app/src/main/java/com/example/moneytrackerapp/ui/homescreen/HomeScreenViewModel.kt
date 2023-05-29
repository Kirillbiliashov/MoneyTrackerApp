package com.example.moneytrackerapp.ui.homescreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HomeScreenUIState(
    val dropdownExpanded: Boolean = false,
    val calendarTypeIdx: Int = 0,
    val chosenDate: String = HomeScreenUtils.getCurrentDate()
)

class HomeScreenViewModel : ViewModel() {
    private var _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState
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
        _uiState.update {
            it.copy(
                calendarTypeIdx = newIdx,
                dropdownExpanded = false
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


}