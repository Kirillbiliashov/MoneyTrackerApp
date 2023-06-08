package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.repo.UserCurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestUserCurrencyRepository : UserCurrencyRepository {

    var saved = false
        private set

    override suspend fun saveCurrency(currencyName: String) {
        saved = true
    }

    override val currency: Flow<String>
        get() = MutableStateFlow("EUR")
}