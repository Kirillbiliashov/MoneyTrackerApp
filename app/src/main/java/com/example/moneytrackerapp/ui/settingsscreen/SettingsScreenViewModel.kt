package com.example.moneytrackerapp.ui.settingsscreen

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.repo.IncomeRepository
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class IncomeUIState(
    val currentIncomeSum: String = "",
    val isIncomeSumValid: Boolean = true,
    val incomeDialogDisplayed: Boolean = false,
    val incomeHistoryDisplayed: Boolean = false
)

data class LimitUIState(
    val currentLimitSum: String = "",
    val isLimitSumValid: Boolean = true,
    val limitDialogDisplayed: Boolean = false,
    val limitsDisplayed: Boolean = false
)

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val limitRepository: LimitRepository,
    private val incomeRepository: IncomeRepository
) : ViewModel() {

    private val _limitUiState = MutableStateFlow(LimitUIState())
    val limitUIState: StateFlow<LimitUIState> = _limitUiState

    private val _incomeUIState = MutableStateFlow(IncomeUIState())
    val incomeUIState: StateFlow<IncomeUIState> = _incomeUIState

    var limits: StateFlow<List<Limit>> = limitRepository.getLimitsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = listOf()
        )
        private set

    var incomeHistory: StateFlow<List<Income>> = incomeRepository.getAll()
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
        _limitUiState.update {
            it.copy(
                currentLimitSum = newSumStr,
                isLimitSumValid = isLimitSumValid
            )
        }
    }

    fun updateIncomeSum(newSumStr: String) {
        val isIncomeSumValid = try {
            newSumStr.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
        _incomeUIState.update {
            it.copy(
                currentIncomeSum = newSumStr,
                isIncomeSumValid = isIncomeSumValid
            )
        }
    }

    fun saveLimit(rate: Double, chosenDate: LocalDate) {
        if (_limitUiState.value.isValid()) {
            viewModelScope.launch {
                val limit = _limitUiState.value.toLimit(rate, chosenDate)
                limitRepository.saveLimit(limit)
            }
        }
    }

    fun saveIncome(rate: Double) {
        if (_incomeUIState.value.isValid()) {
            viewModelScope.launch {
                val income = _incomeUIState.value.toIncome(rate)
                if (!incomeHistory.value.contains(income)) {
                    incomeRepository.saveIncome(income)
                }
            }
        }
    }

    fun toggleDisplayLimits() {
        val limitsDisplayed = _limitUiState.value.limitsDisplayed
        _limitUiState.update { it.copy(limitsDisplayed = !limitsDisplayed) }
    }

    fun hideIncomeDialog() {
        _incomeUIState.update { it.copy(incomeDialogDisplayed = false) }
    }

    fun toggleIncomeHistory() {
        val incomeHistoryDisplayed = _incomeUIState.value.incomeHistoryDisplayed
        _incomeUIState.update {
            it.copy(incomeHistoryDisplayed = !incomeHistoryDisplayed)
        }
    }

    fun showIncomeDialog() {
        _incomeUIState.update { it.copy(incomeDialogDisplayed = true) }
    }

    fun showLimitDialog() {
        _limitUiState.update { it.copy(limitDialogDisplayed = true) }
    }

    fun hideLimitDialog() {
        _limitUiState.update { it.copy(limitDialogDisplayed = false) }
    }

}

@VisibleForTesting
internal fun LimitUIState.isValid() =
    currentLimitSum.isNotEmpty() && isLimitSumValid

@VisibleForTesting
internal fun IncomeUIState.isValid() =
    currentIncomeSum.isNotEmpty() && isIncomeSumValid

private fun LimitUIState.toLimit(
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

private fun IncomeUIState.toIncome(rate: Double): Income {
    val date = LocalDate.now()
    return Income(
        sum = currentIncomeSum.toDouble() * rate,
        year = date.year,
        month = date.monthValue
    )
}