package ru.mamykin.exchange.presentation

import ru.mamykin.exchange.domain.RateEntity

@Deprecated("Remove when iOS is migrated")
data class CurrencyRatesViewData(
    val rates: List<RateEntity>,
    val currentCurrencyRate: CurrentCurrencyRate? = null,
)