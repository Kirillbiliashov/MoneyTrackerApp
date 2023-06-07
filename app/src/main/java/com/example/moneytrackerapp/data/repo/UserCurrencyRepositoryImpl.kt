package com.example.moneytrackerapp.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


interface UserCurrencyRepository {
    suspend fun saveCurrency(currencyName: String)
    val currency: Flow<String>
}

class UserCurrencyRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserCurrencyRepository {
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