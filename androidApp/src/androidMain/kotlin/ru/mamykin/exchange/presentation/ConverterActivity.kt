package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.android.ext.android.inject
import ru.mamykin.exchange.R

internal class ConverterActivity : AppCompatActivity() {

    private val viewModel: ConverterViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContent {
            ConverterTheme {
                val state by viewModel.stateFlow.collectAsState()
                ConverterScreen(state, viewModel::onCurrencyOrAmountChanged) { finish() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRatesLoading()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRatesLoading()
    }
}