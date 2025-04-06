package ru.mamykin.exchange.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import ru.mamykin.exchange.presentation.ConverterViewModel

@Suppress("unused")
class KoinHelper : KoinComponent {
    private val injectedViewModel: ConverterViewModel by inject()

    fun getViewModel(): ConverterViewModel = injectedViewModel
}

@Suppress("unused")
fun initKoin() {
    startKoin {
        modules(appModule)
    }
}