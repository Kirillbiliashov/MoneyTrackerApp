package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.IncomeDao
import com.example.moneytrackerapp.data.entity.Income
import kotlinx.coroutines.flow.Flow

interface IncomeRepository {

    fun getAll(): Flow<List<Income>>

    suspend fun saveIncome(income: Income)
}

class IncomeRepositoryImpl(private val incomeDao: IncomeDao) : IncomeRepository {
    override fun getAll(): Flow<List<Income>> = incomeDao.getAllOrdered()

    override suspend fun saveIncome(income: Income) {
        incomeDao.saveIncome(income)
    }

}