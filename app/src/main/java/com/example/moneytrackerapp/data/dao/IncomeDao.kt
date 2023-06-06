package com.example.moneytrackerapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneytrackerapp.data.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM income ORDER BY year,month")
    fun getAllOrdered(): Flow<List<Income>>

    @Insert(entity = Income::class)
    suspend fun saveIncome(income: Income)
}