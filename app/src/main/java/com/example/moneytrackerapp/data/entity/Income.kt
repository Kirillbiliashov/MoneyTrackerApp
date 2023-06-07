package com.example.moneytrackerapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sum: Double,
    val year: Int,
    val month: Int
) {

    val yearMonthStr: String
        get() = if (month >= 10) "" else "0$month.$year"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Income) return false
        return this.month == other.month && this.year == other.year
    }

}

