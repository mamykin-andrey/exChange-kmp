package ru.mamykin.exchange.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.domain.RateEntity
import kotlin.test.Test

class ConverterViewModelTest {

    companion object {
        const val TEST_CURRENCY = "USD"
    }

    private val interactor: ConverterInteractor = null!!

    // private val interactor: ConverterInteractor = mockk()
    private val viewModel = ConverterViewModel(interactor)
    private val rates1 = listOf(RateEntity(TEST_CURRENCY, 10f))
    private val rates2 = listOf(RateEntity(TEST_CURRENCY, 20f))

    init {
        Dispatchers.setMain(StandardTestDispatcher())
        // every { interactor.getRates(any(), any()) } returns flowOf(Result.success(rates1))
    }

    @Test
    fun `load rates when started`() {
        viewModel.startRatesLoading()

        // verify { interactor.getRates(null, true) }
        // assertEquals(ratesViewData1, viewModel.rates.value)
    }

    @Test
    fun `stop loading rates when stopped`() {
        val ratesFlow = MutableStateFlow(Result.success(rates1))
        // every { interactor.getRates(null, true) } returns ratesFlow
        viewModel.startRatesLoading()

        viewModel.stopRatesLoading()
        ratesFlow.value = Result.success(rates2)

        // assertEquals(ratesViewData1, viewModel.rates.value)
    }

    @Test
    fun `does nothing when amount str isn't valid`() {
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "abc"))

        // verify(exactly = 0) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `update rates when amount changed`() {
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))

        // verify { interactor.getRates(any(), any()) }
    }

    @Test
    fun `does nothing when the currency amount isn't changed`() {
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))

        // verify(exactly = 1) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `update rates when currency changed`() {
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
        viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate("EUR", "10"))

        // verify(exactly = 2) { interactor.getRates(any(), any()) }
    }

    @Test
    fun `show error when loading failed`() {
        // every { interactor.getRates(any(), any()) } returns flowOf(Result.failure(IllegalStateException("test!")))

        viewModel.startRatesLoading()

        // assertEquals(R.string.error_network, viewModel.error.value)
    }
}