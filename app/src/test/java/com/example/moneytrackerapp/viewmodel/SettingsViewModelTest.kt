package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.ui.settingsscreen.SettingsScreenViewModel
import com.example.moneytrackerapp.ui.settingsscreen.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testLimitRepo = TestLimitRepository()
    private val testIncomeRepo = TestIncomeRepository()

    @Before
    fun setTestDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun resetTestDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun viewModel_initializes_loadsLimitsFromRepo() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val viewModelLimits = viewModel.limits.first()
        Assert.assertEquals(viewModelLimits, TestDatasource.limits)
    }

    @Test
    fun viewModel_initializes_loadsIncomeFromRepo() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val viewModelIncomeHistory = viewModel.incomeHistory.first()
        Assert.assertEquals(viewModelIncomeHistory, TestDatasource.incomeHistory)
    }

    @Test
    fun viewModel_updateInvalidIncomeSum_invalidFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val invalidIncomeSum = "invalid"
        viewModel.updateIncomeSum(invalidIncomeSum)
        Assert.assertEquals(viewModel.incomeUIState.value.currentIncomeSum,
            invalidIncomeSum)
        Assert.assertFalse(viewModel.incomeUIState.value.isIncomeSumValid)
    }


    @Test
    fun viewModel_updateValidIncomeSum_validFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val validIncomeSum = "1000"
        viewModel.updateIncomeSum(validIncomeSum)
        Assert.assertEquals(viewModel.incomeUIState.value.currentIncomeSum,
            validIncomeSum)
        Assert.assertTrue(viewModel.incomeUIState.value.isIncomeSumValid)
    }

    @Test
    fun viewModel_updateInvalidLimitSum_invalidFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val invalidLimitSum = "invalid"
        viewModel.updateLimitSum(invalidLimitSum)
        Assert.assertEquals(viewModel.limitUIState.value.currentLimitSum,
            invalidLimitSum)
        Assert.assertFalse(viewModel.limitUIState.value.isLimitSumValid)
    }


    @Test
    fun viewModel_updateValidLimitSum_validFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val validLimitSum = "1000"
        viewModel.updateLimitSum(validLimitSum)
        Assert.assertEquals(viewModel.limitUIState.value.currentLimitSum,
            validLimitSum)
        Assert.assertTrue(viewModel.limitUIState.value.isLimitSumValid)
    }

    @Test
    fun viewModel_saveInvalidIncome_nothingIsSaved() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val testRate = 1.5
        viewModel.saveIncome(testRate)
        Assert.assertFalse(viewModel.incomeUIState.value.isValid())
        Assert.assertFalse(testIncomeRepo.incomeSaved)
    }

    @Test
    fun viewModel_saveValidIncome_savedFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val testRate = 1.5
        viewModel.updateIncomeSum("899")
        viewModel.saveIncome(testRate)
        Assert.assertTrue(viewModel.incomeUIState.value.isValid())
        Assert.assertTrue(testIncomeRepo.incomeSaved)
    }

    @Test
    fun viewModel_saveInvalidLimit_nothingIsSaved() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val testRate = 1.5
        val chosenDate = LocalDate.now()
        viewModel.saveLimit(testRate, chosenDate)
        Assert.assertFalse(viewModel.limitUIState.value.isValid())
        Assert.assertFalse(testLimitRepo.limitSaved)
    }

    @Test
    fun viewModel_saveValidLimit_savedFlagIsSet() = runTest {
        val viewModel = SettingsScreenViewModel(limitRepository = testLimitRepo,
            incomeRepository = testIncomeRepo)
        val testRate = 1.5
        val chosenDate = LocalDate.now()
        viewModel.updateLimitSum("15.99")
        viewModel.saveLimit(testRate, chosenDate)
        Assert.assertTrue(viewModel.limitUIState.value.isValid())
        Assert.assertTrue(testLimitRepo.limitSaved)
    }

}