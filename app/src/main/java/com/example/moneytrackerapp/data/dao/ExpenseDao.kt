package com.example.moneytrackerapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query(
        "SELECT expense.id AS id, expense.name AS name, sum, date, note, " +
                "category.name AS category_name FROM expense JOIN category ON " +
                "category_id = category.id WHERE date BETWEEN :startDate AND :endDate"
    )
    fun getAllByDate(startDate: Long, endDate: Long): Flow<List<ExpenseTuple>>

}