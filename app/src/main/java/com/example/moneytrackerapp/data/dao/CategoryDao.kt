package com.example.moneytrackerapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.moneytrackerapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

}