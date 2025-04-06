package ru.mamykin.exchange.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mamykin.exchange.internal.Closeable
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.domain.RateEntity
import ru.mamykin.exchange.logDebug
import ru.mamykin.exchange.subscribeClosable

class ConverterViewModel(
    private val interactor: ConverterInteractor,
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var ratesJob: Job? = null
    private var currentCurrency: CurrentCurrencyRate? = null

    @Deprecated("Remove when iOS is migrated")
    val isLoading = MutableStateFlow(true)

    @Deprecated("Remove when iOS is migrated")
    val rates = MutableStateFlow<CurrencyRatesViewData?>(null)

    @Deprecated("Remove when iOS is migrated")
    val error = MutableStateFlow<String?>(null)

    @Deprecated("Remove when iOS is migrated")
    val currentRateChanged = MutableStateFlow<Unit?>(null)

    private val mutableStateFlow = MutableStateFlow<ConverterScreenState>(ConverterScreenState.Loading)
    private val state: ConverterScreenState
        get() = mutableStateFlow.value
    val stateFlow: StateFlow<ConverterScreenState> = mutableStateFlow

    // sealed class Effect {
    //     data object CurrentRateChanged : Effect()
    // }

    @Suppress("unused")
    fun observeState(onEach: (state: ConverterScreenState) -> Unit): Closeable {
        return stateFlow.subscribeClosable(viewModelScope, onEach)
    }

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
                val rateViewData = CurrencyRatesViewData(it, currentCurrency)
                rates.value = rateViewData
                mutableStateFlow.value =
                    ConverterScreenState.Loaded(rateViewData.rates.map {
                        CurrencyRateViewData.fromDomainModel(
                            it,
                            currentCurrency
                        )
                    })
                logDebug("ConverterViewModel", "onRatesLoaded: $state")
                if (currencyChanged) {
                    currentRateChanged.value = Unit
                    // TODO: effect
                }
            },
            onFailure = {
                mutableStateFlow.value = ConverterScreenState.Error
                error.value = "Network error, please try again"
                ratesJob?.cancel() // let the user retry when needed
            },
        )
    }
}

sealed class ConverterScreenState {
    data object Loading : ConverterScreenState()
    data object Error : ConverterScreenState()
    data class Loaded(
        val rates: List<CurrencyRateViewData>,
    ) : ConverterScreenState()
}