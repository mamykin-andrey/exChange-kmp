package ru.mamykin.exchange

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.mamykin.exchange.di.appModule

internal class ConverterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@ConverterApp)
            modules(appModule)
        }
    }
}