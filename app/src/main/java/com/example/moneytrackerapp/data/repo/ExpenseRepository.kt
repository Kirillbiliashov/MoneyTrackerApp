package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpensesFlow(): Flow<List<ExpenseTuple>>
}


class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {

    override fun getAllExpensesFlow(): Flow<List<ExpenseTuple>> {
        return expenseDao.getAllByDate()
    }

}