package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.ui.expensescreen.ExpenseScreenViewModel
import com.example.moneytrackerapp.ui.expensescreen.isValid
import com.example.moneytrackerapp.viewmodel.repository.TestCategoriesRepository
import com.example.moneytrackerapp.viewmodel.repository.TestExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ExpenseViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCategoriesRepo = TestCategoriesRepository()
    private val testExpenseRepo = TestExpenseRepository()

    @Before
    fun setTestDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun resetTestDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModel_initializes_loadsCategoriesFromRepo() = runTest {
        val viewModel = ExpenseScreenViewModel(categoryRepo = testCategoriesRepo,
            expenseRepo = testExpenseRepo)
        Assert.assertEquals(viewModel.categories, TestDatasource.categories)
    }

    @Test
    fun viewModel_updateValidExpenseSum_ValidFlagIsSet() = runTest {
        val viewModel = ExpenseScreenViewModel(categoryRepo = testCategoriesRepo,
            expenseRepo = testExpenseRepo)
        val newSumStr = "4.29"
        viewModel.updateExpenseSum(newSumStr)
        Assert.assertEquals(viewModel.uiState.value.sum, newSumStr)
        Assert.assertTrue(viewModel.uiState.value.isSumValid)
    }

    @Test
    fun viewModel_updateInvalidExpenseSum_invalidFlagIsSet() = runTest {
        val viewModel = ExpenseScreenViewModel(categoryRepo = testCategoriesRepo,
            expenseRepo = testExpenseRepo)
        val newSumStr = "invalid"
        viewModel.updateExpenseSum(newSumStr)
        Assert.assertEquals(viewModel.uiState.value.sum, newSumStr)
        Assert.assertFalse(viewModel.uiState.value.isSumValid)
    }

    @Test
    fun viewModel_saveInvalidExpense_NothingIsSaved() = runTest {
        val viewModel = ExpenseScreenViewModel(categoryRepo = testCategoriesRepo,
            expenseRepo = testExpenseRepo)
        val testRate = 1.5
        viewModel.saveExpense(testRate)
        Assert.assertFalse(viewModel.uiState.value.isValid())
        Assert.assertFalse(testExpenseRepo.expenseSaved)
    }

    @Test
    fun viewModel_saveValidExpense_expenseIsSaved() = runTest {
        val viewModel = ExpenseScreenViewModel(
            categoryRepo = testCategoriesRepo,
            expenseRepo = testExpenseRepo
        )
        viewModel.updateExpenseSum("5.99")
        viewModel.updateExpenseName("T-shirt")
        viewModel.updateExpenseCategory(TestDatasource.categories.first())
        val testRate = 1.5
        viewModel.saveExpense(testRate)
        Assert.assertTrue(viewModel.uiState.value.isValid())
        Assert.assertTrue(testExpenseRepo.expenseSaved)
    }

}