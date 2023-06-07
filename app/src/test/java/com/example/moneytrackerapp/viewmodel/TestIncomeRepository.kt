package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.repo.IncomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestIncomeRepository: IncomeRepository {
    var incomeSaved = false
    private set
    override fun getAll() = MutableStateFlow(TestDatasource.incomeHistory)

    override suspend fun saveIncome(income: Income) {
        incomeSaved = true
    }
}