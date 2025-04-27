package ru.mamykin.exchange.data.network

import ApiKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.mamykin.exchange.internal.OpenForTesting
import ru.mamykin.exchange.logDebug

@OpenForTesting
open class RatesNetworkClient {

    companion object {
        const val BASE_URL = "https://api.exchangeratesapi.io/"
        private const val TAG = "RatesNetworkClient"
    }

    private val apiKey = ApiKey.VALUE
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    logDebug(TAG, message)
                }
            }
            level = LogLevel.ALL
        }
    }

    open suspend fun getRates(
        // requestedCurrencyCodes: String = "",
        requestedCurrencyCodes: String = "USD,EUR,GBP,CAD,AUD,CHF,JPY,CNY,INR,KRW,BRL,MXN,RUB,AED,SAR,HKD,NZD,ZAR,SEK,NOK,SGD,TRY,PLN,IDR,THB",
        // requestedCurrencyCodes: String = "RUB,EUR,USD,JPY",
    ): RateListResponse {
        val response =
            httpClient.get("$BASE_URL/v1/latest?access_key=$apiKey&format=1&symbols=$requestedCurrencyCodes")
        return response.body()
    }
}