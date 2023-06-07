package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestExpenseRepository : ExpenseRepository {

    var expenseSaved = false
        private set

    override fun getAllExpensesFlow(): Flow<List<ExpenseTuple>> = MutableStateFlow(listOf())

    override suspend fun saveExpense(expense: Expense) {
        expenseSaved = true
    }

}