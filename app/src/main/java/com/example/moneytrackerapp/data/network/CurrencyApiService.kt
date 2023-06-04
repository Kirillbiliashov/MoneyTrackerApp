package com.example.moneytrackerapp.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {

    @GET("{currency}.json")
    suspend fun getCurrencyRates(@Path("currency") currency: String): String

}