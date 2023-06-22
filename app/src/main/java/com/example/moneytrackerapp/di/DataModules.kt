package com.example.moneytrackerapp.di

import android.content.Context
import androidx.room.Room
import com.example.moneytrackerapp.data.db.AppDatabase
import com.example.moneytrackerapp.data.network.CurrencyApiService
import com.example.moneytrackerapp.data.repo.CategoryRepository
import com.example.moneytrackerapp.data.repo.CategoryRepositoryImpl
import com.example.moneytrackerapp.data.repo.CurrencyRepository
import com.example.moneytrackerapp.data.repo.CurrencyRepositoryImpl
import com.example.moneytrackerapp.data.repo.ExpenseRepository
import com.example.moneytrackerapp.data.repo.ExpenseRepositoryImpl
import com.example.moneytrackerapp.data.repo.IncomeRepository
import com.example.moneytrackerapp.data.repo.IncomeRepositoryImpl
import com.example.moneytrackerapp.data.repo.LimitRepository
import com.example.moneytrackerapp.data.repo.LimitRepositoryImpl
import com.example.moneytrackerapp.data.repo.SaveFileRepository
import com.example.moneytrackerapp.data.repo.UserCurrencyRepository
import com.example.moneytrackerapp.data.repo.UserCurrencyRepositoryImpl
import com.example.moneytrackerapp.data.repo.WorkerManagerSaveFileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepository: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepository: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepository: ExpenseRepositoryImpl
    ): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindIncomeRepository(
        incomeRepository: IncomeRepositoryImpl
    ): IncomeRepository

    @Binds
    @Singleton
    abstract fun bindLimitRepository(
        limitRepository: LimitRepositoryImpl
    ): LimitRepository

    @Binds
    @Singleton
    abstract fun bindSaveFileRepository(
        saveFileRepository: WorkerManagerSaveFileRepository
    ): SaveFileRepository

    @Binds
    @Singleton
    abstract fun bindUserCurrencyRepository(
        userCurrencyRepository: UserCurrencyRepositoryImpl
    ): UserCurrencyRepository

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctxt: Context) = Room
        .databaseBuilder(ctxt, AppDatabase::class.java, "money_tracker")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCategoryDao(database: AppDatabase) = database.getCategoryDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase) = database.getExpenseDao()

    @Provides
    fun provideIncomeDao(database: AppDatabase) = database.getIncomeDao()

    @Provides
    fun provideLimitDao(database: AppDatabase) = database.getLimitDao()

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val BASE_URL =
        "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/"

    @Singleton
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    @Provides
    fun provideCurrencyApiService(retrofit: Retrofit) =
        retrofit.create(CurrencyApiService::class.java)
}