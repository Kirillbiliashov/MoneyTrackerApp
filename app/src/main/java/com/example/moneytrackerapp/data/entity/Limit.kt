package com.example.moneytrackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "limit")
data class Limit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sum: Double,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDte: Long
)
