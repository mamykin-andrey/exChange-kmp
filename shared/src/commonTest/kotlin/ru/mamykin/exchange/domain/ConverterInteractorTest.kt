// package ru.mamykin.exchange.domain
//
// import com.nhaarman.mockito_kotlin.whenever
// import io.reactivex.Single
// import io.reactivex.android.plugins.RxAndroidPlugins
// import io.reactivex.plugins.RxJavaPlugins
// import org.hamcrest.CoreMatchers.`is`
// import org.hamcrest.MatcherAssert.assertThat
// import org.junit.After
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.mockito.Mock
// import org.mockito.MockitoAnnotations
// import ru.mamykin.exchange.data.RatesRepository
// import kotlin.util.*
// import kotlin.util.concurrent.TimeUnit
//
// class ConverterInteractorTest {
//
//     companion object {
//         const val RUB_CURRENCY = "RUB"
//     }
//
//     @get:Rule
//     val testSchedulerRule = TestSchedulerRule()
//     @Mock
//     lateinit var ratesRepository: RatesRepository
//
//     val todayDate = Date()
//     val rateList1 = RateList(RUB_CURRENCY, todayDate, listOf(RateEntity("USD", 0.015f)))
//     val rateList2 = RateList(RUB_CURRENCY, todayDate, listOf(RateEntity("EUR", 0.013f)))
//     val rateList3 = RateList(RUB_CURRENCY, todayDate, listOf(RateEntity("USD", 0.015f), RateEntity("EUR", 0.013f)))
//     lateinit var interactor: ConverterInteractor
//
//     @Before
//     fun setUp() {
//         MockitoAnnotations.initMocks(this)
//         interactor = ConverterInteractor(ratesRepository)
//         whenever(ratesRepository.getRates(true))
//                 .thenReturn(Single.just(rateList1), Single.just(rateList2))
//     }
//
//     @After
//     fun tearDown() {
//         RxJavaPlugins.reset()
//         RxAndroidPlugins.reset()
//     }
//
//     @Test
//     fun getRates_shouldReturnOneItemEverySecond() {
//         val testObserver = interactor.getRates(RUB_CURRENCY, 1f, true).test()
//         testSchedulerRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
//
//         // Because first item emits without timeout
//         testObserver.assertNoErrors().assertValueCount(3)
//     }
//
//     @Test
//     fun getRates_shouldDoesNotStopUpdates_whenErrorOccurs() {
//         whenever(ratesRepository.getRates(true)).thenReturn(
//                 Single.just(rateList1),
//                 Single.error(RuntimeException()),
//                 Single.just(rateList2)
//         )
//
//         val testObserver = interactor.getRates(RUB_CURRENCY, 1f, true).test()
//         testSchedulerRule.testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)
//
//         testObserver.assertNoErrors().assertValueCount(4)
//     }
//
//     @Test
//     fun getRates_shouldAddCurrentCurrencyRateToTopOfList() {
//         val testObserver = interactor.getRates(RUB_CURRENCY, 1f, true).test()
//         testSchedulerRule.testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)
//
//         testObserver.assertNoErrors().assertValue { it.rates.first().code == RUB_CURRENCY }
//     }
//
//     @Test
//     fun getRates_shouldReturnCurrenciesAmountWithExchangeRate() {
//         val expectedRates = listOf(RateEntity("USD", 1.5f), RateEntity("EUR", 1.3000001f))
//         whenever(ratesRepository.getRates(true)).thenReturn(Single.just(rateList3))
//
//         val testObserver = interactor.getRates(RUB_CURRENCY, 100f, true).test()
//         testSchedulerRule.testScheduler.advanceTimeBy(0, TimeUnit.SECONDS)
//
//         testObserver.assertNoErrors().assertValue { it.rates.skip(1) == expectedRates }
//     }
//
//     @Test
//     fun needRecalculate_shouldReturnFalse_whenCurrencyAreEqual() {
//         val needRecalculate = interactor.needRecalculate("EUR", "EUR", 123.25f, 123.25f)
//
//         assertThat(needRecalculate, `is`(false))
//     }
//
//     @Test
//     fun needRecalculate_shouldReturnTrue_whenCurrencyCodesAreNotEqual() {
//         val currencyEquals = interactor.needRecalculate("EUR", "USD", 12.20f, 12.20f)
//
//         assertThat(currencyEquals, `is`(true))
//     }
//
//     @Test
//     fun needRecalculate_shouldReturnTrue_whenAmountDiffMoreThanPrecision() {
//         val currencyEquals = interactor.needRecalculate("USD", "USD", 12.20f, 12.25f)
//
//         assertThat(currencyEquals, `is`(true))
//     }
// }