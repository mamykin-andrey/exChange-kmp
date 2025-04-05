package ru.mamykin.exchange.presentation

data class CurrentCurrencyRate(
    val code: String,
    val amountStr: String,
    val cursorPosition: Int? = null,
)