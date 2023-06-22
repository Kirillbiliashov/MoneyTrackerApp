package com.example.moneytrackerapp.ui.categoriesscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.repo.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriesScreenUIState(
    val chosenCategories: List<Category> = listOf(),
    val dialogShown: Boolean = false,
    val addCategoryName: String = ""
)

@HiltViewModel
class CategoriesScreenViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow(CategoriesScreenUIState())
    val uiState: StateFlow<CategoriesScreenUIState> = _uiState
    val allCategoriesChosen: Boolean
        get() = _uiState.value.chosenCategories.equalsWithoutOrder(categories)

    var categories: List<Category> = listOf()
        private set

    init {
        viewModelScope.launch {
            categoryRepo.getAllCategoriesFlow().collect { data ->
                categories = data
                _uiState.update { it.copy(chosenCategories = categories) }
            }
        }
    }

    fun changeChosenCategory(category: Category) {
        _uiState.update { state ->
            if (allCategoriesChosen) {
                state.copy(chosenCategories = listOf(category))
            } else {
                val chosenCategories = _uiState.value.chosenCategories
                state.copy(
                    chosenCategories =
                    if (!chosenCategories.contains(category)) listOf(
                        *chosenCategories.toTypedArray(),
                        category
                    ) else chosenCategories.filter { it != category }
                )
            }
        }
    }

    fun changeTextFieldValue(newValue: String) {
        _uiState.update { it.copy(addCategoryName = newValue) }
    }

    fun showDialog() {
        _uiState.update { it.copy(dialogShown = true) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogShown = false) }
    }

    fun saveCategory() {
        viewModelScope.launch {
            categoryRepo.saveCategory(_uiState.value.toCategory())
        }
    }

}

private fun CategoriesScreenUIState.toCategory() = Category(name = addCategoryName)

private fun List<Category>.equalsWithoutOrder(categories: List<Category>): Boolean {
    if (this == categories) return true
    if (this.size != categories.size) return false
    forEach { if (!categories.contains(it)) return false }
    return true
}