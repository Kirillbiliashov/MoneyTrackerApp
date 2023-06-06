package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.LimitDao
import com.example.moneytrackerapp.data.entity.Limit
import kotlinx.coroutines.flow.Flow

interface LimitRepository {

    suspend fun saveLimit(limit: Limit)

    fun getLimitsFlow(): Flow<List<Limit>>

}

class LimitRepositoryImpl(private val limitDao: LimitDao) : LimitRepository {
    override suspend fun saveLimit(limit: Limit) {
        limitDao.saveLimit(limit)
    }

    override fun getLimitsFlow(): Flow<List<Limit>> = limitDao.getAllOrdered()

}