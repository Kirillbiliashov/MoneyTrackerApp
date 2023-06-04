package com.example.moneytrackerapp.data.container

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import com.example.moneytrackerapp.data.repo.UserCurrencyRepository
import com.example.moneytrackerapp.data.repo.WorkerManagerSaveFileRepository
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

interface AppContainer {
    val expenseRepository: ExpenseRepository
    val categoryRepository: CategoryRepository
    val limitRepository: LimitRepository
    val saveFileRepository: SaveFileRepository
    val currencyRepository: CurrencyRepository
    val userCurrencyRepository: UserCurrencyRepository
}

private const val CURRENCY_PREFERENCE_NAME = "currency_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CURRENCY_PREFERENCE_NAME
)

class AppDataContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val db = AppDatabase.getDatabase(context)

    override val expenseRepository by lazy {
        ExpenseRepositoryImpl(db.getExpenseDao())
    }

    override val categoryRepository by lazy {
        CategoryRepositoryImpl(db.getCategoryDao())
    }

    override val limitRepository by lazy {
        LimitRepositoryImpl(db.getLimitDao())
    }

    override val saveFileRepository by lazy {
        WorkerManagerSaveFileRepository(context)
    }

    private val retrofitService by lazy {
        retrofit.create(CurrencyApiService::class.java)
    }

    override val currencyRepository by lazy {
        CurrencyRepositoryImpl(retrofitService)
    }

    override val userCurrencyRepository by lazy {
        UserCurrencyRepository(context.dataStore)
    }

}