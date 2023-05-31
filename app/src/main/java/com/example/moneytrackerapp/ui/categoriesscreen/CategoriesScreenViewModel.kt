package com.example.moneytrackerapp.ui.categoriesscreen

import androidx.lifecycle.ViewModel
import com.example.moneytrackerapp.Datasource
import com.example.moneytrackerapp.ui.homescreen.HomeScreenUIState
import com.example.moneytrackerapp.ui.homescreen.HomeScreenUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class CategoriesScreenUIState(
    val chosenCategories: List<String> = Datasource.categories
)

class CategoriesScreenViewModel : ViewModel() {

    private var _uiState = MutableStateFlow(CategoriesScreenUIState())
    val uiState: StateFlow<CategoriesScreenUIState> = _uiState
    val allCategoriesChosen: Boolean
        get() = _uiState.value.chosenCategories == Datasource.categories

    fun changeChosenCategory(category: String) {
        if (allCategoriesChosen) {
            _uiState.update { state ->
                state.copy(
                    chosenCategories = listOf(category)
                )
            }
        } else {
            val chosenCategories = _uiState.value.chosenCategories
            _uiState.update { state ->
                state.copy(
                    chosenCategories = if (!chosenCategories.contains(category)) listOf(
                        *chosenCategories.toTypedArray(),
                        category
                    ) else chosenCategories.filter { it != category }
                )
            }
        }
    }

}