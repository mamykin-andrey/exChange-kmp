// package ru.mamykin.exchange.presentation
//
// import io.reactivex.Observable
// import io.reactivex.subjects.BehaviorSubject
// import org.junit.Assert.assertEquals
// import org.junit.Test
// import org.mockito.Mockito.anyBoolean
// import org.mockito.Mockito.mock
// import org.mockito.Mockito.times
// import org.mockito.Mockito.verify
// import org.mockito.kotlin.anyOrNull
// import org.mockito.kotlin.eq
// import org.mockito.kotlin.never
// import org.mockito.kotlin.whenever
// import ru.mamykin.exchange.R
// import ru.mamykin.exchange.domain.ConverterInteractor
// import ru.mamykin.exchange.domain.RateEntity
//
// class ConverterViewModelTest {
//
//     companion object {
//         const val TEST_CURRENCY = "USD"
//     }
//
//     private val interactor: ConverterInteractor = mock()
//     private val viewDataMapper: RateViewDataMapper = mock()
//     private val viewModel = ConverterViewModel(
//         interactor = interactor,
//         ioScheduler = ioScheduler,
//         mainScheduler = mainScheduler,
//         viewDataMapper = viewDataMapper,
//     )
//     private val rates1 = listOf(RateEntity(TEST_CURRENCY, 10f))
//     private val ratesViewData1 = listOf(CurrencyRatesViewData(TEST_CURRENCY, "10.0", 0, null))
//     private val rates2 = listOf(RateEntity(TEST_CURRENCY, 20f))
//     private val ratesViewData2 = listOf(CurrencyRatesViewData(TEST_CURRENCY, "20.0", 0, null))
//
//     init {
//         whenever(interactor.getRates(anyOrNull(), anyBoolean())).thenReturn(Observable.just(Result.success(rates1)))
//         whenever(viewDataMapper.transform(eq(rates1), anyOrNull())).thenReturn(ratesViewData1)
//     }
//
//     @Test
//     fun `load rates when started`() {
//         viewModel.startRatesLoading()
//
//         verify(interactor).getRates(null, true)
//         assertEquals(ratesViewData1, viewModel.rates.value)
//     }
//
//     @Test
//     fun `stop loading rates when stopped`() {
//         val ratesSubject = BehaviorSubject.createDefault(Result.success(rates1))
//         whenever(interactor.getRates(null, true)).thenReturn(ratesSubject)
//         viewModel.startRatesLoading()
//
//         viewModel.stopRatesLoading()
//         ratesSubject.onNext(Result.success(rates2))
//
//         assertEquals(ratesViewData1, viewModel.rates.value)
//     }
//
//     @Test
//     fun `does nothing when amount str isn't valid`() {
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "abc"))
//
//         verify(interactor, never()).getRates(anyOrNull(), anyBoolean())
//     }
//
//     @Test
//     fun `update rates when amount changed`() {
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
//
//         verify(interactor).getRates(anyOrNull(), anyBoolean())
//     }
//
//     @Test
//     fun `does nothing when the currency amount isn't changed`() {
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
//
//         verify(interactor, times(1)).getRates(anyOrNull(), anyBoolean())
//     }
//
//     @Test
//     fun `update rates when currency changed`() {
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate(TEST_CURRENCY, "10"))
//         viewModel.onCurrencyOrAmountChanged(CurrentCurrencyRate("EUR", "10"))
//
//         verify(interactor, times(2)).getRates(anyOrNull(), anyBoolean())
//     }
//
//     @Test
//     fun `show error when loading failed`() {
//         whenever(interactor.getRates(anyOrNull(), anyBoolean())).thenReturn(
//             Observable.just(Result.failure(IllegalStateException("test!")))
//         )
//
//         viewModel.startRatesLoading()
//
//         assertEquals(R.string.error_network, viewModel.error.value)
//     }
// }