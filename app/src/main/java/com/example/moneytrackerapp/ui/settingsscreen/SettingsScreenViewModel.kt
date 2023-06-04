package com.example.moneytrackerapp.ui.settingsscreen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.ui.expensescreen.ExpenseScreenUIState
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

data class SettingsScreenUIState(
    val currentLimitSum: Double = 0.00,
    val chosenDates: List<LocalDate> = listOf(),
    val limitsDisplayed: Boolean = false,
    val currenciesDisplayed: Boolean = false
)

class SettingsScreenViewModel(private val limitRepository: LimitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUIState())
    val uiState: StateFlow<SettingsScreenUIState> = _uiState

    var limits: StateFlow<List<Limit>> = limitRepository.getLimitsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )

    fun updateLimitSum(newSumStr: String) {
        val newSum = try {
            newSumStr.toDouble()
        } catch (e: NumberFormatException) {
            0.00
        }
        _uiState.update { it.copy(currentLimitSum = newSum) }
    }

    fun updateChosenDates(newDates: List<LocalDate>) {
        _uiState.update { it.copy(chosenDates = newDates) }
    }

    fun saveLimit(rate: Double) {
        viewModelScope.launch {
            val limit = _uiState.value.toLimit(rate)
            limitRepository.saveLimit(limit)
        }
    }

    fun toggleDisplayLimits() {
        val limitsDisplayed = _uiState.value.limitsDisplayed
        _uiState.update { it.copy(limitsDisplayed = !limitsDisplayed) }
    }

    fun toggleDisplayCurrencies() {
        val currenciesDisplayed = _uiState.value.currenciesDisplayed
        _uiState.update { it.copy(currenciesDisplayed = !currenciesDisplayed) }
    }

}

private fun SettingsScreenUIState.toLimit(rate: Double): Limit {
    val startDate = LocalDateTime.of(chosenDates.first(), LocalTime.MIN)
    val endDate = LocalDateTime.of(chosenDates.last(), LocalTime.MAX)
    val startDateMillis = startDate.toMillis()
    val endDateMillis = endDate.toMillis()
    return Limit(
        sum = currentLimitSum * rate,
        startDate = startDateMillis,
        endDte = endDateMillis
    )
}