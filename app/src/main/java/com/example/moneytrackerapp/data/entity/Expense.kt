package com.example.moneytrackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense",
    foreignKeys = [ForeignKey(
        entity = Category::class, parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Expense(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val sum: Double,
    val date: Long,
    val note: String? = null,
    @ColumnInfo(name = "category_id") val categoryId: Int
)
