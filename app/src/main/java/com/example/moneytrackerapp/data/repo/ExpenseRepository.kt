package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface ExpenseRepository {
    fun getAllExpensesFlow(): Flow<List<ExpenseTuple>>
    suspend fun saveExpense(expense: Expense)
}

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpensesFlow(): Flow<List<ExpenseTuple>> {
        return expenseDao.getAll()
    }

    override suspend fun saveExpense(expense: Expense) {
        expenseDao.saveExpense(expense)
    }

}