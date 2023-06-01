package com.example.moneytrackerapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moneytrackerapp.MoneyTrackerApplication
import com.example.moneytrackerapp.ui.categoriesscreen.CategoriesScreenViewModel
import com.example.moneytrackerapp.ui.expensescreen.ExpenseScreenViewModel
import com.example.moneytrackerapp.ui.homescreen.HomeScreenViewModel

object ViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel(moneyTrackerApp().container.expenseRepository)
        }
        initializer {
            ExpenseScreenViewModel(moneyTrackerApp().container.categoryRepository)
        }
        initializer {
            CategoriesScreenViewModel(moneyTrackerApp().container.categoryRepository)
        }
    }
}

fun CreationExtras.moneyTrackerApp(): MoneyTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MoneyTrackerApplication)
