package ru.mamykin.exchange.data

import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import ru.mamykin.exchange.data.network.RateListResponse
import ru.mamykin.exchange.data.network.RatesNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals

class RatesRepositoryTest {

    private val ratesApi: RatesNetworkClient = mock()

    private val ratesResponse1 = RateListResponse(
        base = "EUR",
        rates = listOf(
            "RUB" to 100f,
            "USD" to 1f,
        ).toMap()
    )
    private val ratesList1 = ratesResponse1.toDomainModel()
    private val ratesResponse2 = RateListResponse(
        base = "EUR",
        rates = listOf(
            "RUB" to 100f,
            "USD" to 0.9f,
        ).toMap()
    )
    private val ratesList2 = ratesResponse2.toDomainModel()

    private val repository = RatesRepository(ratesApi)

    init {
        everySuspend { ratesApi.getRates() } sequentially {
            returns(ratesResponse1)
            returns(ratesResponse2)
        }
    }

    @Test
    fun `return data from remote when cache is empty`() = runTest {
        val response = repository.getRates(false)

        assertEquals(ratesList1.size, response.size)
        assertEquals(ratesList1, response)
    }

    @Test
    fun `return data from cache when force is false`() = runTest {
        // update cache with response1
        repository.getRates(false)

        val rates = repository.getRates(false)

        assertEquals(ratesList1.size, rates.size)
        assertEquals(ratesList1, rates)
    }

    @Test
    fun `return data from remote when force is true`() = runTest {
        // update cache with response1
        repository.getRates(false)

        val rates = repository.getRates(true)

        assertEquals(ratesList2.size, rates.size)
        assertEquals(ratesList2, rates)
    }
}