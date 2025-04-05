package ru.mamykin.exchange.presentation

import ru.mamykin.exchange.domain.RateEntity
import ru.mamykin.exchange.formatNumber

data class CurrencyRateViewData(
    val code: String,
    val amountStr: String,
    val cursorPosition: Int? = null
) {
    companion object {

        fun fromDomainModel(
            rate: RateEntity,
            currentCurrency: CurrentCurrencyRate?,
        ): CurrencyRateViewData {
            val formattedAmount = if (rate.code == currentCurrency?.code)
                currentCurrency.amountStr
            else
                formatNumber(rate.amount)
            val cursorPosition = if (rate.code == currentCurrency?.code) {
                currentCurrency.cursorPosition
            } else
                null
            return CurrencyRateViewData(
                code = rate.code,
                amountStr = formattedAmount,
                cursorPosition = cursorPosition,
            )
        }
    }
}