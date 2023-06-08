package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.ui.homescreen.HomeScreenViewModel
import com.example.moneytrackerapp.utils.CalendarOption
import com.example.moneytrackerapp.utils.Currency
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.DateUtils
import com.example.moneytrackerapp.viewmodel.repository.TestCurrencyRepository
import com.example.moneytrackerapp.viewmodel.repository.TestExpenseRepository
import com.example.moneytrackerapp.viewmodel.repository.TestSaveFileRepository
import com.example.moneytrackerapp.viewmodel.repository.TestUserCurrencyRepository
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
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testExpenseRepo = TestExpenseRepository()
    private val testSaveFileRepo = TestSaveFileRepository()
    private val testCurrencyRepo = TestCurrencyRepository()
    private val testUserCurrencyRepo = TestUserCurrencyRepository()

    @Before
    fun setTestDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun resetTestDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModel_initializes_expensesAreLoaded() {
        val viewModel = getViewModel()
        Assert.assertEquals(viewModel.expenses, TestDatasource.expenses)
    }

    @Test
    fun viewModel_initializes_correctCurrencyRateIsSet() {
        val currencyRate = CurrencyRate(currency = Currency.EUR, rate = 1.5)
        val viewModel = getViewModel()
        Assert.assertEquals(viewModel.uiState.value.currentCurrencyRate, currencyRate)
    }

    @Test
    fun viewModel_updateChosenDate_chosenExpensesAreUpdated() {
        val viewModel = getViewModel()
        Assert.assertTrue(viewModel.uiState.value.displayExpenses.isEmpty())
        viewModel.updateChosenDate(DateUtils.toLocalDate(1686119760031))
        Assert.assertEquals(
            viewModel.uiState.value.displayExpenses,
            TestDatasource.expenses.subList(0, 2)
        )
    }

    @Test
    fun viewModel_dropdownOptionChanges_uiUpdatesCorrectly() = runTest {
        val viewModel = getViewModel()
        viewModel.changeDropdownOption(2)
        val formatter = DateTimeFormatter.ofPattern("MMM\nyyyy")
        val expChosenDate = YearMonth.now().format(formatter)
        Assert.assertEquals(viewModel.uiState.value.calendarOption, CalendarOption.MONTHLY)
        Assert.assertEquals(expChosenDate, viewModel.uiState.value.chosenDate)
    }

    private fun getViewModel() = HomeScreenViewModel(
        testExpenseRepo,
        testSaveFileRepo,
        testCurrencyRepo,
        testUserCurrencyRepo
    )

}