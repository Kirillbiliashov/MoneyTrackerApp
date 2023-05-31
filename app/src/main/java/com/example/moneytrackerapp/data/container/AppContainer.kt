package com.example.moneytrackerapp.data.container

import android.content.Context
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepositoryImpl

interface AppContainer {
    val expenseRepository: ExpenseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepositoryImpl(AppDatabase.getDatabase(context).getExpenseDao())
    }

}