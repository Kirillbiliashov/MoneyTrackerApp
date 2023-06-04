package com.example.moneytrackerapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moneytrackerapp.MoneyTrackerApplication
import com.example.moneytrackerapp.ui.categoriesscreen.CategoriesScreenViewModel
import com.example.moneytrackerapp.ui.expensescreen.ExpenseScreenViewModel
import com.example.moneytrackerapp.ui.homescreen.HomeScreenViewModel
import com.example.moneytrackerapp.ui.settingsscreen.SettingsScreenViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(
                expenseRepository =
                moneyTrackerApp().container.expenseRepository,
                saveFileRepository = moneyTrackerApp().container.saveFileRepository,
                currencyRepository = moneyTrackerApp().container.currencyRepository
            )
        }
        initializer {
            ExpenseScreenViewModel(
                categoryRepo = moneyTrackerApp().container.categoryRepository,
                expenseRepo = moneyTrackerApp().container.expenseRepository
            )
        }
        initializer {
            CategoriesScreenViewModel(moneyTrackerApp().container.categoryRepository)
        }
        initializer {
            SettingsScreenViewModel(moneyTrackerApp().container.limitRepository)
        }
    }
}

fun CreationExtras.moneyTrackerApp(): MoneyTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoneyTrackerApplication)
