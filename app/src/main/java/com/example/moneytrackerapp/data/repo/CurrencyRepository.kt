package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.network.CurrencyApiService
import com.example.moneytrackerapp.utils.Currency

interface CurrencyRepository {
    suspend fun getCurrencyRates(currency: Currency): String
}

class CurrencyRepositoryImpl(
    private val currencyApiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getCurrencyRates(currency: Currency) =
        currencyApiService.getCurrencyRates(currency.toString().lowercase())

}