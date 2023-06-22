package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.LimitDao
import com.example.moneytrackerapp.data.entity.Limit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface LimitRepository {

    suspend fun saveLimit(limit: Limit)

    fun getLimitsFlow(): Flow<List<Limit>>

}

@Singleton
class LimitRepositoryImpl @Inject constructor(
    private val limitDao: LimitDao
) : LimitRepository {
    override suspend fun saveLimit(limit: Limit) {
        limitDao.saveLimit(limit)
    }

    override fun getLimitsFlow(): Flow<List<Limit>> = limitDao.getAllOrdered()

}