package ru.mamykin.exchange.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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

    private val mutableEffectFlow = MutableSharedFlow<ConverterScreenEffect>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effectFlow = mutableEffectFlow.asSharedFlow()

    @Suppress("unused")
    fun observeEffect(onEach: (effect: ConverterScreenEffect) -> Unit): Closeable {
        return effectFlow.subscribeClosable(viewModelScope, onEach)
    }

    @Suppress("unused")
    fun observeState(onEach: (state: ConverterScreenState) -> Unit): Closeable {
        return stateFlow.subscribeClosable(viewModelScope, onEach)
    }

    // TODO: Move to the screen instead of activity
    fun startRatesLoading() {
        loadRates(null, true)
    }

    // TODO: Move to the screen instead of activity
    fun stopRatesLoading() {
        ratesJob?.cancel()
        ratesJob = null
    }

    fun onIntent(intent: ConverterScreenIntent) = viewModelScope.launch {
        when (intent) {
            is ConverterScreenIntent.CurrencyOrAmountChanged -> {
                onCurrencyOrAmountChanged(intent.currencyRate)
            }

            is ConverterScreenIntent.RetryLoading -> {
                startRatesLoading()
            }
        }
    }

    private fun onCurrencyOrAmountChanged(currencyRate: CurrentCurrencyRate) {
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
                logDebug("ConverterViewModel", "onRatesLoaded: $it")
                mutableStateFlow.value =
                    ConverterScreenState.Loaded(it.map {
                        CurrencyRateViewData.fromDomainModel(
                            it,
                            currentCurrency
                        )
                    })
                if (currencyChanged) {
                    mutableEffectFlow.tryEmit(ConverterScreenEffect.CurrentRateChanged)
                }
            },
            onFailure = {
                logDebug("ConverterViewModel", "onRatesFailed: $it")
                mutableStateFlow.value = ConverterScreenState.Error
                ratesJob?.cancel() // let the user retry when needed
            },
        )
    }
}

sealed class ConverterScreenIntent {
    data class CurrencyOrAmountChanged(val currencyRate: CurrentCurrencyRate) : ConverterScreenIntent()
    data object RetryLoading : ConverterScreenIntent()
}

sealed class ConverterScreenEffect {
    data object CurrentRateChanged : ConverterScreenEffect()
}

sealed class ConverterScreenState {
    data object Loading : ConverterScreenState()
    data object Error : ConverterScreenState()
    data class Loaded(
        val rates: List<CurrencyRateViewData>,
    ) : ConverterScreenState()
}