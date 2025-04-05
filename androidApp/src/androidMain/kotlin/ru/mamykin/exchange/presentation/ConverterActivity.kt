package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.mamykin.exchange.R
import ru.mamykin.exchange.core.di.Scopes
import toothpick.Toothpick

internal class ConverterActivity : AppCompatActivity() {

    private val viewModel by lazy {
        Toothpick.openScopes(Scopes.APP_SCOPE, this).getInstance(ConverterViewModel::class.java)
    }

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

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            Toothpick.closeScope(this)
        }
    }
}