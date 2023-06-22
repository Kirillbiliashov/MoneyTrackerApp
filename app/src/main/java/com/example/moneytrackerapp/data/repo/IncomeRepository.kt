package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.IncomeDao
import com.example.moneytrackerapp.data.entity.Income
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface IncomeRepository {

    fun getAll(): Flow<List<Income>>

    suspend fun saveIncome(income: Income)
}

@Singleton
class IncomeRepositoryImpl @Inject constructor(
    private val incomeDao: IncomeDao
) : IncomeRepository {
    override fun getAll(): Flow<List<Income>> {
        return incomeDao.getAllOrdered()
    }

    override suspend fun saveIncome(income: Income) {
        incomeDao.saveIncome(income)
    }

}