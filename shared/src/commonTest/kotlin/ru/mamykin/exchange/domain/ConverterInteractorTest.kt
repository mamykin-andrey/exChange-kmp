package ru.mamykin.exchange.domain

// import io.mockk.coEvery
// import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import ru.mamykin.exchange.data.RatesRepository
import ru.mamykin.exchange.presentation.CurrentCurrencyRate
import kotlin.test.Test
import kotlin.test.assertEquals

class ConverterInteractorTest {

    companion object {
        const val TEST_BASE_CURRENCY = "RUB"
    }

    private val ratesRepository: RatesRepository = null!!
    // private val ratesRepository: RatesRepository = mockk()

    private val rateList1 =
        listOf(RateEntity(TEST_BASE_CURRENCY, 100f), RateEntity("USD", 1.1f), RateEntity("EUR", 1f))
    private val rateList2 =
        listOf(RateEntity(TEST_BASE_CURRENCY, 100f), RateEntity("USD", 1.2f), RateEntity("EUR", 1f))

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testCoroutineDispatcher = StandardTestDispatcher(testCoroutineScheduler)
    private val interactor = ConverterInteractor(ratesRepository)

    init {
        // coEvery { ratesRepository.getRates(any()) } returnsMany listOf(rateList1, rateList2)
    }

    @Test
    fun getRates_shouldReturnDataPerSetTimeWindow() = runTest(testCoroutineDispatcher) {
        var emissionsCount = 0
        val job = launch {
            interactor.getRates(CurrentCurrencyRate(TEST_BASE_CURRENCY, "1.0", null), false)
                .flowOn(testCoroutineDispatcher)
                .collect { emissionsCount++ }
        }

        testCoroutineScheduler.advanceTimeBy(ConverterInteractor.EXCHANGE_UPDATE_PERIOD_MS + 1_000)
        job.cancel()

        assertEquals(2, emissionsCount)
    }

    @Test
    fun getRates_shouldDoesNotStopUpdates_whenErrorOccurs() = runTest(testCoroutineScheduler) {
        // coEvery { ratesRepository.getRates(true) } returns rateList1
        // coEvery { ratesRepository.getRates(true) } throws RuntimeException()
        // coEvery { ratesRepository.getRates(true) } returns rateList2
        var emissionsCount = 0
        val job = launch {
            interactor.getRates(CurrentCurrencyRate(TEST_BASE_CURRENCY, "1.0", null), false)
                .flowOn(testCoroutineDispatcher)
                .collect { emissionsCount++ }
        }

        testCoroutineScheduler.advanceTimeBy(ConverterInteractor.EXCHANGE_UPDATE_PERIOD_MS * 2 + 1_000)
        job.cancel()

        assertEquals(3, emissionsCount)
    }

    @Test
    fun getRates_shouldMoveCurrentCurrencyRateToTopOfList() = runTest(testCoroutineDispatcher) {
        val result = interactor.getRates(CurrentCurrencyRate(TEST_BASE_CURRENCY, "1.0", null), true)
            .flowOn(testCoroutineDispatcher).take(1).first()

        assertEquals(result.getOrThrow().first().code, TEST_BASE_CURRENCY)
    }

    @Test
    fun getRates_shouldReturnCurrenciesAmountWithExchangeRate() = runTest {
        val expectedRates = listOf(
            RateEntity(code = "RUB", amount = 200f),
            RateEntity(code = "USD", amount = 2.2f),
            RateEntity(code = "EUR", amount = 2f)
        )

        val result = interactor.getRates(CurrentCurrencyRate(TEST_BASE_CURRENCY, "200", null), false).first()

        assertEquals(Result.success(expectedRates), result)
    }
}