package ru.mamykin.exchange.data

import ru.mamykin.exchange.data.network.RateListResponse
import ru.mamykin.exchange.data.network.RatesNetworkClient

class FakeRatesNetworkClient : RatesNetworkClient() {

    private val getRatesResponses = mutableListOf<RateListResponse>()

    fun everyGetRatesReturn(vararg responses: RateListResponse) {
        getRatesResponses.addAll(responses)
    }

    override suspend fun getRates(requestedCurrencyCodes: String): RateListResponse {
        if (getRatesResponses.isEmpty()) throw IllegalStateException("Please init the mock responses first!")
        val response = getRatesResponses.last()
        if (getRatesResponses.size > 1) {
            val newList = getRatesResponses.dropLast(1)
            getRatesResponses.clear()
            getRatesResponses.addAll(newList)
        }
        return response
    }
}