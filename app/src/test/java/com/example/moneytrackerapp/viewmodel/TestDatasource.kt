package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Category

object TestDatasource {
    val categories = listOf(
        Category(id = 1, name = "Groceries"),
        Category(id = 2, name = "Education"),
        Category(id = 3, name = "Entertainment"),
    )
}