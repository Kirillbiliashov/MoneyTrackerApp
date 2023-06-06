package com.example.moneytrackerapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sum: Double,
    val year: Int,
    val month: Int
)
