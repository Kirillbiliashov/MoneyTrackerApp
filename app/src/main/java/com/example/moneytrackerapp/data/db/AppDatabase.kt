package com.example.moneytrackerapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneytrackerapp.data.dao.CategoryDao
import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.dao.IncomeDao
import com.example.moneytrackerapp.data.dao.LimitDao
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.Expense
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.entity.Limit


@Database(version = 7, entities = [Category::class, Expense::class, Limit::class, Income::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getExpenseDao(): ExpenseDao
    abstract fun getLimitDao(): LimitDao
    abstract fun getIncomeDao(): IncomeDao

}