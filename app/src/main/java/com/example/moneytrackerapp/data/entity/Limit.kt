package com.example.moneytrackerapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Entity(tableName = "limit")
data class Limit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sum: Double,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDte: Long
)


fun Limit.localDateRangeString(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    dateFormat.timeZone = TimeZone.getDefault()
    val startDate = Date(startDate)
    val endDate = Date(endDte)
    val startDateStr = dateFormat.format(startDate)
    val endDateStr = dateFormat.format(endDate)
    if (startDateStr == endDateStr) return startDateStr
    return "$startDateStr - $endDateStr"
}