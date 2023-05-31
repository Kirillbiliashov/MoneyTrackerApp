package com.example.moneytrackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class ExpenseTuple(
    val id: Long,
    val name: String,
    val sum: Double,
    val date: Long,
    val note: String? = null,
    @ColumnInfo(name = "category_name") val categoryName: String
)