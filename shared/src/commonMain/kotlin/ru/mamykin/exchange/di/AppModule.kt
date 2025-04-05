package ru.mamykin.exchange.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.mamykin.exchange.data.RatesRepository
import ru.mamykin.exchange.data.network.RatesNetworkClient
import ru.mamykin.exchange.domain.ConverterInteractor
import ru.mamykin.exchange.presentation.ConverterViewModel

val appModule = module {
    singleOf(::RatesNetworkClient)
    factoryOf(::RatesRepository)
    factoryOf(::ConverterInteractor)
    factoryOf(::ConverterViewModel)
}