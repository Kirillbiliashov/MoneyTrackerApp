package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.ui.categoriesscreen.CategoriesScreenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testRepo = TestCategoriesRepository()

    @Before
    fun setTestDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun resetTestDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModel_initializes_setsChosenCategoriesFromRepo() = runTest {
        val viewModel = CategoriesScreenViewModel(testRepo)
        Assert.assertEquals(viewModel.categories, TestDatasource.categories)
        Assert.assertTrue(viewModel.allCategoriesChosen)
        Assert.assertEquals(
            viewModel.uiState.value.chosenCategories,
            TestDatasource.categories
        )
    }

    @Test
    fun viewModel_changeChosenCategories_newChosenCategoriesStored() = runTest {
        val viewModel = CategoriesScreenViewModel(testRepo)
        val firstCategory = TestDatasource.categories[0]
        val secondCategory = TestDatasource.categories[1]
        viewModel.changeChosenCategory(firstCategory)
        Assert.assertEquals(viewModel.uiState.value.chosenCategories, listOf(firstCategory))
        Assert.assertFalse(viewModel.allCategoriesChosen)
        viewModel.changeChosenCategory(secondCategory)
        Assert.assertEquals(
            viewModel.uiState.value.chosenCategories,
            listOf(firstCategory, secondCategory)
        )
        Assert.assertFalse(viewModel.allCategoriesChosen)
        viewModel.changeChosenCategory(secondCategory)
        Assert.assertEquals(
            viewModel.uiState.value.chosenCategories,
            listOf(firstCategory)
        )
        Assert.assertFalse(viewModel.allCategoriesChosen)
    }

    @Test
    fun viewModel_saveCategory_repositoryMethodCalled() = runTest {
        val viewModel = CategoriesScreenViewModel(testRepo)
        viewModel.saveCategory()
        Assert.assertTrue(testRepo.saveMethodCalled)
    }

    @Test
    fun viewModel_changeTextFieldCalled_newValueSavedToUIState() = runTest {
        val viewModel = CategoriesScreenViewModel(testRepo)
        Assert.assertEquals(viewModel.uiState.value.addCategoryName, "")
        val newValue = "abc"
        viewModel.changeTextFieldValue(newValue)
        Assert.assertEquals(viewModel.uiState.value.addCategoryName, newValue)
    }

}