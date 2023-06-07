package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.repo.LimitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestLimitRepository: LimitRepository {
    var limitSaved = false
    private set
    override suspend fun saveLimit(limit: Limit) {
        limitSaved = true
    }

    override fun getLimitsFlow() = MutableStateFlow(TestDatasource.limits)
}