package com.example.moneytrackerapp.utils

enum class Currency {
    USD {
        override val sign = "$"
    },
    EUR {
        override val sign = "€"
    },
    UAH {
        override val sign = "₴"
    };
    abstract val sign: String
}