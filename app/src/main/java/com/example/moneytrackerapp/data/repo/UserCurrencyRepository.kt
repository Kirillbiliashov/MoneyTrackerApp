package com.example.moneytrackerapp.data.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


interface UserCurrencyRepository {
    suspend fun saveCurrency(currencyName: String)
    val currency: Flow<String>
}

private const val CURRENCY_PREFERENCE_NAME = "currency_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = CURRENCY_PREFERENCE_NAME
)

@Singleton
class UserCurrencyRepositoryImpl @Inject constructor(
    @ApplicationContext private val ctxt: Context
) : UserCurrencyRepository {

    private val dataStore: DataStore<Preferences> = ctxt.dataStore

    private companion object {
        val CURRENCY_NAME = stringPreferencesKey("currency_name")
    }

    override val currency: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[CURRENCY_NAME] ?: "USD"
        }

    override suspend fun saveCurrency(currencyName: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_NAME] = currencyName
        }
    }
}