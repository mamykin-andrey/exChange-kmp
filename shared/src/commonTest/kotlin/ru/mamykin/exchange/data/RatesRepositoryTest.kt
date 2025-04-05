package ru.mamykin.exchange.data

import kotlinx.coroutines.test.runTest
import ru.mamykin.exchange.data.network.RateListResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class RatesRepositoryTest {

    private val ratesApi = FakeRatesNetworkClient()
    private val rates1 = listOf(
        "RUB" to 100f,
        "USD" to 1f,
    )
    private val rates2 = listOf(
        "RUB" to 100f,
        "USD" to 0.9f,
    )
    private val ratesResponse1 = RateListResponse("EUR", rates1.toMap())
    private val ratesResponse2 = RateListResponse("EUR", rates2.toMap())
    private val repository = RatesRepository(ratesApi)

    init {
        ratesApi.everyGetRatesReturn(ratesResponse1, ratesResponse2)
    }

    @Test
    fun `return data from remote when cache is empty`() = runTest {
        val response = repository.getRates(false)

        assertEquals(rates1.size, response.size)
        assertEquals(rates1.first(), response.first().let { it.code to it.amount })
        assertEquals(rates1[1], response[1].let { it.code to it.amount })
    }

    @Test
    fun `return data from cache when force is false`() = runTest {
        // updates cache and uses rates/response 1
        repository.getRates(false)

        val rates = repository.getRates(false)

        assertEquals(rates1.size, rates.size)
        assertEquals(rates1.first(), rates.first().let { it.code to it.amount })
        assertEquals(rates1[1], rates[1].let { it.code to it.amount })
    }

    @Test
    fun `return data from remote when force is true`() = runTest {
        // updates cache and uses rates/response 1
        repository.getRates(false)

        val rates = repository.getRates(true)

        assertEquals(rates2.size, rates.size)
        assertEquals(rates2.first(), rates.first().let { it.code to it.amount })
        assertEquals(rates2[1], rates[1].let { it.code to it.amount })
    }
}