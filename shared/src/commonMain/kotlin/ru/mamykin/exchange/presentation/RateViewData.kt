package ru.mamykin.exchange.presentation

import ru.mamykin.exchange.domain.RateEntity
import ru.mamykin.exchange.formatNumber

// TODO: Rename to CurrencyRateViewData
data class RateViewData(
    val code: String,
    val amountStr: String,
    val cursorPosition: Int? = null
) {
    companion object {

        fun fromDomainModel(
            rate: RateEntity,
            currentCurrency: CurrentCurrencyRate?,
        ): RateViewData {
            val formattedAmount = if (rate.code == currentCurrency?.code)
                currentCurrency.amountStr
            else
                formatNumber(rate.amount)
            val cursorPosition = if (rate.code == currentCurrency?.code) {
                currentCurrency.cursorPosition
            } else
                null
            return RateViewData(
                code = rate.code,
                amountStr = formattedAmount,
                cursorPosition = cursorPosition,
            )
        }
    }
}