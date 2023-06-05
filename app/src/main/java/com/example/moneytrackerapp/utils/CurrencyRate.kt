package com.example.moneytrackerapp.utils


data class CurrencyRate(
    val currency: Currency = Currency.USD,
    val rate: Double = 1.0
)


fun CurrencyRate.formatSum(sum: Double) = currency.sign +
        (sum / rate).formatOutput()

fun Number.formatOutput() = String.format("%.2f", this)