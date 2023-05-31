package com.example.moneytrackerapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category(
    @PrimaryKey val id: Int = 0,
    val name: String
)
