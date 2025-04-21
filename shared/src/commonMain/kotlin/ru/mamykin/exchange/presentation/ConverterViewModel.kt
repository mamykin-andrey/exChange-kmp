package ru.mamykin.exchange.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.domain.RateEntity
import ru.mamykin.exchange.internal.Closeable
import ru.mamykin.exchange.logDebug
import ru.mamykin.exchange.subscribeClosable

class ConverterViewModel(
    private val interactor: ConverterInteractor,
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var ratesJob: Job? = null
    private var currentCurrency: CurrentCurrencyRate? = null

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

    fun startRatesLoading() {
        loadRates(null, true)
    }

    fun stopRatesLoading() {
        ratesJob?.cancel()
        ratesJob = null
    }

    fun onCurrencyOrAmountChanged(currencyRate: CurrentCurrencyRate) {
        currencyRate.amountStr.replace(",", ".").toFloatOrNull() ?: return
        if (currencyRate.code == currentCurrency?.code && currencyRate.amountStr == currentCurrency?.amountStr) return

        val currencyChanged = currencyRate.code != currentCurrency?.code
        this.currentCurrency = currencyRate
        loadRates(currencyRate, currencyChanged)
    }

    private fun loadRates(
        currentCurrency: CurrentCurrencyRate?,
        currencyChanged: Boolean,
    ) {
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
                mutableStateFlow.value =
                    ConverterScreenState.Loaded(it.map {
                        CurrencyRateViewData.fromDomainModel(
                            it,
                            currentCurrency
                        )
                    })
                logDebug("ConverterViewModel", "onRatesLoaded: $state")
                // TODO: current rate changed effect if (currencyChanged)
            },
            onFailure = {
                mutableStateFlow.value = ConverterScreenState.Error
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