package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.network.CurrencyApiService
import com.example.moneytrackerapp.utils.Currency
import javax.inject.Inject
import javax.inject.Singleton

interface CurrencyRepository {
    suspend fun getCurrencyRates(currency: Currency): String
}

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService
) : CurrencyRepository {

    override suspend fun getCurrencyRates(currency: Currency) =
        currencyApiService.getCurrencyRates(currency.toString().lowercase())

}