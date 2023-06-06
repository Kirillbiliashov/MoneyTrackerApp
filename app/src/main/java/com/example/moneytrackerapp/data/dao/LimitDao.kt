package com.example.moneytrackerapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneytrackerapp.data.entity.Limit
import kotlinx.coroutines.flow.Flow

@Dao
interface LimitDao {

    @Insert(entity = Limit::class)
    suspend fun saveLimit(limit: Limit)

    @Query("SELECT * FROM `limit` ORDER BY start_date,end_date")
    fun getAllOrdered(): Flow<List<Limit>>
}