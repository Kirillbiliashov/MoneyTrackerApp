package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.repo.IncomeRepository
import com.example.moneytrackerapp.viewmodel.TestDatasource
import kotlinx.coroutines.flow.MutableStateFlow

class TestIncomeRepository: IncomeRepository {
    var incomeSaved = false
    private set
    override fun getAll() = MutableStateFlow(TestDatasource.incomeHistory)

    override suspend fun saveIncome(income: Income) {
        incomeSaved = true
    }
}