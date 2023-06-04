package com.example.moneytrackerapp.data.container

import android.content.Context
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.network.CurrencyApiService
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.data.repo.CategoryRepositoryImpl
import com.example.moneytrackerapp.data.repo.CurrencyRepository
import com.example.moneytrackerapp.data.repo.CurrencyRepositoryImpl
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepositoryImpl
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.data.repo.LimitRepositoryImpl
import com.example.moneytrackerapp.data.repo.SaveFileRepository
import com.example.moneytrackerapp.data.repo.WorkerManagerSaveFileRepository
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
    val expenseRepository: ExpenseRepository
    val categoryRepository: CategoryRepository
    val limitRepository: LimitRepository
    val saveFileRepository: SaveFileRepository
    val currencyRepository: CurrencyRepository

}

class AppDataContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

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

    private val retrofitService: CurrencyApiService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }

    override val currencyRepository: CurrencyRepository by lazy {
        CurrencyRepositoryImpl(retrofitService)
    }

}