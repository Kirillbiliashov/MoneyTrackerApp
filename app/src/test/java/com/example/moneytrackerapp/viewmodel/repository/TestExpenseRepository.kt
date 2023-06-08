package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.viewmodel.TestDatasource
import kotlinx.coroutines.flow.MutableStateFlow

class TestExpenseRepository : ExpenseRepository {

    var expenseSaved = false
        private set

    override fun getAllExpensesFlow() = MutableStateFlow(TestDatasource.expenses)

    override suspend fun saveExpense(expense: Expense) {
        expenseSaved = true
    }

}