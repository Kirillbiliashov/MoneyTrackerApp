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


class UserCurrencyRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val CURRENCY_NAME = stringPreferencesKey("currency_name")
    }

    val currency: Flow<String> = dataStore.data
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

    suspend fun saveCurrency(currencyName: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_NAME] = currencyName
        }
    }
}