package ru.mamykin.exchange.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import ru.mamykin.exchange.data.network.RateListResponse
import ru.mamykin.exchange.data.network.RatesNetworkClient
import kotlin.test.Test
import kotlin.test.assertEquals

class RatesRepositoryTest {

    private val ratesApi: RatesNetworkClient = mockk()
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
        coEvery { ratesApi.getRates(any()) } returnsMany (listOf(ratesResponse1, ratesResponse2))
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

        val it = repository.getRates(false)

        assertEquals(rates1.size, it.size)
        assertEquals(rates1.first(), it.first().let { it.code to it.amount })
        assertEquals(rates1[1], it[1].let { it.code to it.amount })
    }

    @Test
    fun `return data from remote when force is true`() = runTest {
        // updates cache and uses rates/response 1
        repository.getRates(false)

        val it = repository.getRates(true)

        assertEquals(rates2.size, it.size)
        assertEquals(rates2.first(), it.first().let { it.code to it.amount })
        assertEquals(rates2[1], it[1].let { it.code to it.amount })
    }
}