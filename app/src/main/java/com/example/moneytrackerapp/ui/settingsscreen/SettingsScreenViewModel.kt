package com.example.moneytrackerapp.ui.settingsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class SettingsScreenUIState(
    val currentLimitSum: String = "",
    val isLimitSumValid: Boolean = true,
    val limitsDisplayed: Boolean = false
)

class SettingsScreenViewModel(
    private val limitRepository: LimitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsScreenUIState())
    val uiState: StateFlow<SettingsScreenUIState> = _uiState

    var limits: StateFlow<List<Limit>> = limitRepository.getLimitsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )
        private set

    fun updateLimitSum(newSumStr: String) {
        val isLimitSumValid = try {
            newSumStr.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
        _uiState.update {
            it.copy(
                currentLimitSum = newSumStr,
                isLimitSumValid = isLimitSumValid
            )
        }
    }


    fun saveLimit(rate: Double, chosenDate: LocalDate) {
        if (_uiState.value.isValid()) {
            viewModelScope.launch {
                val limit = _uiState.value.toLimit(rate, chosenDate)
                limitRepository.saveLimit(limit)
            }
        }
    }

    fun toggleDisplayLimits() {
        val limitsDisplayed = _uiState.value.limitsDisplayed
        _uiState.update { it.copy(limitsDisplayed = !limitsDisplayed) }
    }

}

private fun SettingsScreenUIState.isValid() =
    currentLimitSum.isNotEmpty() && isLimitSumValid

private fun SettingsScreenUIState.toLimit(
    rate: Double,
    chosenDate: LocalDate
): Limit {
    val startDateMillis = chosenDate.atTime(LocalTime.MIN).toMillis()
    val endDateMillis = chosenDate.atTime(LocalTime.MAX).toMillis()
    return Limit(
        sum = currentLimitSum.toDouble() * rate,
        startDate = startDateMillis,
        endDte = endDateMillis
    )
}