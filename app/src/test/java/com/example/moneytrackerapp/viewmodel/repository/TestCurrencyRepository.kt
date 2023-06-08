package com.example.moneytrackerapp.viewmodel.repository

import com.example.moneytrackerapp.data.repo.CurrencyRepository
import com.example.moneytrackerapp.utils.Currency

class TestCurrencyRepository: CurrencyRepository {
    override suspend fun getCurrencyRates(currency: Currency): String = """
  {
    "date": "2023-06-06",
    "eur": {
        "ghs": 12.054587,
        "gip": 0.860651,
        "gmd": 63.741744,
        "gnf": 9190.922468,
        "gno": 0.009707,
        "grt": 9.372425,
        "gt": 0.266967,
        "gtq": 8.372018,
        "gyd": 226.131378,
        "hbar": 22.169834,
        "hkd": 8.401126,
        "hnl": 26.295917,
        "usd": 1.5,
        "usdc": 1.07152,
        "usdp": 1.07024,
        "usdt": 1.071099,
        "uyu": 41.494942,
        "uzs": 12242.707232
    }
}
    """.trimIndent()
}