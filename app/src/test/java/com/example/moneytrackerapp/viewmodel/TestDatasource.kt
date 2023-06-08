package com.example.moneytrackerapp.viewmodel

import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.entity.Limit

object TestDatasource {
    val categories = listOf(
        Category(id = 1, name = "Groceries"),
        Category(id = 2, name = "Education"),
        Category(id = 3, name = "Entertainment"),
    )
    val limits = listOf(
        Limit(id = 1, 19.99, 1000, 2000),
        Limit(id = 2, 29.99, 3000, 5000),
        Limit(id = 3, 14.99, 6000, 7000),
    )
    val incomeHistory = listOf(
        Income(id = 1, 800.0, 2023, 5),
        Income(id = 2, 700.0, 2023, 2),
        Income(id = 3, 1000.0, 2023, 4)
    )
    val expenses = listOf(
        ExpenseTuple(
            id = 1,
            name = "Xbox",
            sum = 399.99,
            date =  1686119760031,
            categoryName = "Entertainment"
        ),
        ExpenseTuple(
            id = 2,
            name = "Book",
            sum = 8.99,
            date = 1686119760031,
            categoryName = "Education"
        ),
        ExpenseTuple(
            id = 3,
            name = "Meat",
            sum = 4.99,
            date = 900000000,
            categoryName = "Groceries"
        ),
    )
}