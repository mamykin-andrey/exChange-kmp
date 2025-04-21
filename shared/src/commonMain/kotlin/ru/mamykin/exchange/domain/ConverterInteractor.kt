package ru.mamykin.exchange.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.mamykin.exchange.catchSafely
import ru.mamykin.exchange.data.RatesRepository
import ru.mamykin.exchange.internal.VisibleForTesting
import ru.mamykin.exchange.presentation.CurrentCurrencyRate

class ConverterInteractor(
    private val ratesRepository: RatesRepository,
) {
    companion object {
        @VisibleForTesting
        const val EXCHANGE_UPDATE_PERIOD_MS = 300_000L
        private const val API_BASE_CURRENCY_CODE = "EUR" // limitations of the API free plan
    }

    /**
     * @param baseCurrency code and amount of the currency, which is used to calculate the exchange rates for all other currencies
     * @return exchange rates calculated to the current currency, if it's not provided, then EUR will be used
     */
    fun getRates(
        baseCurrency: CurrentCurrencyRate?,
        currencyChanged: Boolean,
    ): Flow<Result<List<RateEntity>>> = flow {
        while (true) {
            val rates = ratesRepository.getRates(currencyChanged)
            val calculatedRates = calculateExchangeRate(
                rates,
                baseCurrency?.code,
                baseCurrency?.amountStr?.toFloat(),
            )
            val sortedRates = moveCurrentCurrencyToTop(calculatedRates, baseCurrency?.code)
            emit(Result.success(sortedRates))
            delay(EXCHANGE_UPDATE_PERIOD_MS)
        }
    }.catchSafely { emit(Result.failure(it)) }

    private fun calculateExchangeRate(
        rates: List<RateEntity>,
        baseCode: String?,
        baseAmount: Float?,
    ): List<RateEntity> {
        if (baseAmount == null) return rates

        val toApiBaseExchangeRate = 1 / rates.find { it.code == baseCode }!!.amount
        return rates.map { currency ->
            val toCurrencyExchangeRate = rates.find { it.code == currency.code }!!.amount
            if (currency.code == baseCode) currency.copy(amount = baseAmount)
            else currency.copy(amount = toApiBaseExchangeRate * toCurrencyExchangeRate * baseAmount)
        }
    }

    private fun moveCurrentCurrencyToTop(
        rates: List<RateEntity>,
        currentCurrency: String?,
    ): List<RateEntity> {
        val currentCurrency = currentCurrency ?: API_BASE_CURRENCY_CODE

        val currentRate = rates.find { it.code == currentCurrency }
        if (currentRate == null) return rates

        return listOf(currentRate) + rates.filter { it.code != currentCurrency }
    }
}