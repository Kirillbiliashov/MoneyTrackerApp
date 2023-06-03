package com.example.moneytrackerapp.data.container

import android.content.Context
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.data.repo.CategoryRepositoryImpl
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepositoryImpl
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.data.repo.LimitRepositoryImpl
import com.example.moneytrackerapp.data.repo.SaveFileRepository
import com.example.moneytrackerapp.data.repo.WorkerManagerSaveFileRepository

interface AppContainer {
    val expenseRepository: ExpenseRepository
    val categoryRepository: CategoryRepository
    val limitRepository: LimitRepository
    val saveFileRepository: SaveFileRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepositoryImpl(AppDatabase.getDatabase(context).getExpenseDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(AppDatabase.getDatabase(context).getCategoryDao())
    }

    override val limitRepository: LimitRepository by lazy {
        LimitRepositoryImpl(AppDatabase.getDatabase(context).getLimitDao())
    }

    override val saveFileRepository: SaveFileRepository by lazy {
        WorkerManagerSaveFileRepository(context)
    }

}