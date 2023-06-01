package com.example.moneytrackerapp.ui.categoriesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.repo.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoriesScreenUIState(
    val chosenCategories: List<Category> = listOf()
)

class CategoriesScreenViewModel(private val categoryRepo: CategoryRepository) : ViewModel() {

    private var _uiState = MutableStateFlow(CategoriesScreenUIState())
    val uiState: StateFlow<CategoriesScreenUIState> = _uiState
    val allCategoriesChosen: Boolean
        get() = _uiState.value.chosenCategories == categories

     var categories: List<Category> = listOf()
    private set

    init {
        viewModelScope.launch {
            categoryRepo.getAllCategoriesFlow().collect {
                categories = it
                _uiState.update { it.copy(chosenCategories = categories) }
            }
        }
    }


    fun changeChosenCategory(category: Category) {
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