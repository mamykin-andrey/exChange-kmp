package ru.mamykin.exchange.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mamykin.exchange.Closeable
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.domain.RateEntity
import ru.mamykin.exchange.subscribeClosable

class ConverterViewModel(
    private val interactor: ConverterInteractor,
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    private var ratesJob: Job? = null
    private var currentCurrency: CurrentCurrencyRate? = null

    val isLoading = MutableStateFlow(true)
    val rates = MutableStateFlow<CurrencyRatesViewData?>(null)
    val error = MutableStateFlow<String?>(null)
    val currentRateChanged = MutableStateFlow<Unit?>(null)

    @Suppress("unused")
    fun observeRates(onEach: (CurrencyRatesViewData?) -> Unit): Closeable {
        return rates.subscribeClosable(viewModelScope, onEach)
    }

    @Suppress("unused")
    fun observeIsLoading(onEach: (Boolean) -> Unit): Closeable {
        return isLoading.subscribeClosable(viewModelScope, onEach)
    }

    @Suppress("unused")
    fun observeError(onEach: (String?) -> Unit): Closeable {
        return error.subscribeClosable(viewModelScope, onEach)
    }

    @Suppress("unused")
    fun observeCurrentRateChanged(onEach: (Unit?) -> Unit): Closeable {
        return currentRateChanged.subscribeClosable(viewModelScope, onEach)
    }

    fun startRatesLoading() {
        loadRates(null, true)
    }

    fun stopRatesLoading() {
        ratesJob?.cancel()
        ratesJob = null
    }

    fun onCurrencyOrAmountChanged(currencyRate: CurrentCurrencyRate) {
        currencyRate.amountStr.toFloatOrNull() ?: return
        if (currencyRate.code == currentCurrency?.code && currencyRate.amountStr == currentCurrency?.amountStr) return

        val currencyChanged = currencyRate.code != currentCurrency?.code
        this.currentCurrency = currencyRate
        loadRates(currencyRate, currencyChanged)
    }

    private fun loadRates(
        currentCurrency: CurrentCurrencyRate?,
        currencyChanged: Boolean,
    ) {
        isLoading.value = true
        ratesJob?.cancel()
        ratesJob = interactor.getRates(currentCurrency, currencyChanged).onEach {
            isLoading.value = false
            onRatesLoaded(it, currentCurrency, currencyChanged)
        }.launchIn(viewModelScope)
    }

    private fun onRatesLoaded(
        result: Result<List<RateEntity>>,
        currentCurrency: CurrentCurrencyRate?,
        currencyChanged: Boolean,
    ) {
        result.fold(
            onSuccess = {
                rates.value = CurrencyRatesViewData(it, currentCurrency)
                if (currencyChanged) {
                    currentRateChanged.value = Unit
                }
            },
            onFailure = {
                // error.postValue(R.string.error_network)
                error.value = "Network error, please try again"
                ratesJob?.cancel() // let the user retry when needed
            },
        )
    }
}