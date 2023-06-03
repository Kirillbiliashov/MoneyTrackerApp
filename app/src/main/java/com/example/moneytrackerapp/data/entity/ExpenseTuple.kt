package com.example.moneytrackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.moneytrackerapp.utils.DateUtils
import java.time.LocalDate

data class ExpenseTuple(
    val id: Int,
    val name: String,
    val sum: Double,
    val date: Long,
    val note: String? = null,
    @ColumnInfo(name = "category_name") val categoryName: String
) {
    override fun toString(): String {
        val noteOutput = if (note == null) "." else "; Note: $note."
        return "Expense: $name; Category: $categoryName; Sum: $sum;" +
                " Date: ${DateUtils.toLocalDate(date)}$noteOutput"
    }
}