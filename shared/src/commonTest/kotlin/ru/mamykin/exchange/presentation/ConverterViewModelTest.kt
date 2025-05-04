package ru.mamykin.exchange.presentation

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.domain.RateEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    companion object {
        const val TEST_CURRENCY = "USD"
    }

    private val interactor: ConverterInteractor = mock()
    private val viewModel = ConverterViewModel(interactor)
    private val ratesList1 = listOf(RateEntity(TEST_CURRENCY, 10f))

    init {
        Dispatchers.setMain(StandardTestDispatcher())
        everySuspend { interactor.getRates(any(), any()) } returns flowOf(Result.success(ratesList1))
    }

    @Test
    fun `load rates when started`() = runTest {
        viewModel.onIntent(ConverterScreenIntent.StartLoading)
        advanceUntilIdle()

        verify { interactor.getRates(null, true) }
        assertEquals(1, viewModel.stateFlow.replayCache.size)
        val state = viewModel.stateFlow.value
        assertTrue {
            state is ConverterScreenState.Loaded &&
                state.rates.size == 1 &&
                state.rates[0].code == TEST_CURRENCY &&
                state.rates[0].amountStr == "10.00"
        }
    }

    @Test
    fun `does nothing when amount str isn't valid`() = runTest {
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "abc")))
        advanceUntilIdle()

        verify(exactly(0)) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `update rates when amount changed`() = runTest {
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10")))
        advanceUntilIdle()

        verify { interactor.getRates(any(), any()) }
    }

    @Test
    fun `does nothing when the currency amount isn't changed`() = runTest {
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10")))
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10")))
        advanceUntilIdle()

        verify(exactly(1)) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `update rates when currency changed`() = runTest {
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10")))
        viewModel.onIntent(ConverterScreenIntent.CurrencyOrAmountChanged(CurrentCurrencyRate("EUR", "10")))
        advanceUntilIdle()

        verify(exactly(2)) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `show error when loading failed`() = runTest {
        every { interactor.getRates(any(), any()) } returns flowOf(Result.failure(IllegalStateException("test!")))

        viewModel.onIntent(ConverterScreenIntent.StartLoading)
        advanceUntilIdle()

        assertEquals(ConverterScreenState.Error, viewModel.stateFlow.value)
    }
}